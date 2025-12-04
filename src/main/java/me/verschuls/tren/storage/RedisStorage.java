package me.verschuls.tren.storage;

import de.exlll.configlib.YamlConfigurations;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.Getter;
import me.verschuls.cbu.CM;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.config.Redis;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.utils.CompressionUtil;
import me.verschuls.tren.utils.Logger;
import me.verschuls.tren.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RedisStorage extends StorageHandler {

    private static final String KEY_PREFIX = "moggedkits:cooldown:";

    @Getter
    private final String instanceId = UUID.randomUUID().toString().substring(0, 8);

    private final Redis.Data config;
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisAsyncCommands<String, String> commands;

    private RedisPubSub pubSub;

    // kit sync tracking - order doesn't matter, just count
    private final Set<String> receivedKits = ConcurrentHashMap.newKeySet();
    private final AtomicInteger expectedKitCount = new AtomicInteger(0);

    // prevents broadcast loop when receiving synced configs
    private final AtomicBoolean syncing = new AtomicBoolean(false);

    public RedisStorage(JavaPlugin plugin, Executor executor, Redis.Data config) {
        super(plugin, executor);

        this.config = config;

        Logger.info("Initiating Redis storage...");
        Logger.debug("Redis config - host: {}, port: {}, db: {}, ssl: {}", config.getHost(), config.getPort().toString(), String.valueOf(config.getDatabase()), String.valueOf(config.isSsl()));

        try {
            Logger.debug("Building RedisURI...");
            RedisURI.Builder uriBuilder = RedisURI.builder()
                    .withHost(config.getHost())
                    .withPort(config.getPort())
                    .withDatabase(config.getDatabase())
                    .withTimeout(Duration.ofMillis(config.getTimeout()))
                    .withClientName(config.getClientName()+"-"+instanceId)
                    .withSsl(config.isSsl());

            if (config.getPassword() != null && !config.getPassword().isEmpty()) {
                Logger.debug("Password provided, user: {}", config.getUser() != null && !config.getUser().isEmpty() ? config.getUser() : "(none)");
                if (config.getUser() != null && !config.getUser().isEmpty()) uriBuilder.withAuthentication(config.getUser(), config.getPassword().toCharArray());
                else uriBuilder.withPassword(config.getPassword().toCharArray());
            } else {
                Logger.debug("No password configured");
            }

            Logger.debug("Creating RedisClient...");
            this.client = RedisClient.create(uriBuilder.build());
            Logger.debug("RedisClient created, attempting connection...");
            this.connection = client.connect();
            Logger.debug("Connection established, getting async commands...");
            this.commands = connection.async();

            Logger.success("Redis storage connected to {}:{}", config.getHost(), config.getPort().toString());
            Logger.debug("Initializing PubSub...");
            this.pubSub = new RedisPubSub(client, instanceId);
            Logger.debug("PubSub initialized, setting up reload listeners...");
            reloadListeners();
            Logger.debug("Reload listeners registered");
        } catch (Exception e) {
            Logger.error("Failed to connect to Redis at {}:{}", e, config.getHost(), config.getPort().toString());
            MoggedKits.disable();
        }
    }

    private List<File> getKits() {
        try (var stream = Files.walk(MoggedKits.getInstance().getDataPath().resolve("kits"))) {
            return stream.filter(p -> {
                if (!Files.isRegularFile(p)) return false;
                String name = p.getFileName().toString();
                return name.endsWith(".yml") && !(name.startsWith("_") && name.endsWith("_.yml"));
            }).map(Path::toFile).toList();
        } catch (Exception e) {
            Logger.error("Failed to read kit files from disk", e);
            return List.of();
        }
    }

    private void reloadListeners() {
        Logger.debug("Registering Config reload listener...");
        CM.onReload(Config.class, (data -> {
            if (syncing.get()) return;
            Logger.debug("Config reloaded, broadcasting to other instances...");
            File file = MoggedKits.getInstance().getDataPath().resolve("config.yml").toFile();
            broadcastFile("config", file);
        }));
        Logger.debug("Registering Messages reload listener...");
        CM.onReload(Messages.class, (data -> {
            if (syncing.get()) return;
            Logger.debug("Messages reloaded, broadcasting to other instances...");
            File file = MoggedKits.getInstance().getDataPath().resolve("messages.yml").toFile();
            broadcastFile("messages", file);
        }));
        Logger.debug("Registering KitManager reload listener...");
        KitManager.onReload(manager -> {
            if (syncing.get()) return;
            List<File> files = getKits();
            Logger.debug("KitManager reloaded, broadcasting {} kits to other instances...", String.valueOf(files.size()));
            for (File file : files) {
                broadcastFile(Utils.args("kit-{}-{}", Utils.cleanName(file), String.valueOf(files.size())), file);
            }
        });

        Logger.debug("Subscribing to file-sync channel...");
        pubSub.subscribe("file-sync", payload -> {
            Logger.debug("Received file-sync payload, length: {}", String.valueOf(payload.length()));
            String[] info = payload.split("\\|");
            Logger.debug("File-sync type: {}", info[0]);
            AtomicReference<byte[]> data = new AtomicReference<>();
            try {
                data.set(CompressionUtil.decompressToFile(info[1]));
                Logger.debug("Decompressed {} bytes", String.valueOf(data.get().length));
            } catch (IOException e) {
                Logger.error("Failed to decompress {}.yml", e, info[0]);
                return;
            }
            if (info[0].startsWith("kit-")) {
                String[] kitInfo = info[0].split("-");
                String name = kitInfo[1];
                int size = Integer.parseInt(kitInfo[2]);
                expectedKitCount.set(size);
                Logger.info("Syncing kit: {} ({}/{})", name, String.valueOf(receivedKits.size() + 1), String.valueOf(size));
                try {
                    Files.write(MoggedKits.getInstance().getDataPath().resolve("kits/" + name + ".yml"), data.get());
                    receivedKits.add(name);
                    // check if all kits arrived (order doesn't matter)
                    if (receivedKits.size() == expectedKitCount.get()) {
                        // delete kits that weren't in the update
                        for (File kit : getKits()) {
                            if (receivedKits.contains(Utils.cleanName(kit))) continue;
                            kit.delete();
                        }
                        syncing.set(true);
                        try {
                            KitManager.get().reload();
                        } finally {
                            syncing.set(false);
                        }
                        Logger.success("Kit sync complete! {} kits: {}", String.valueOf(receivedKits.size()), String.valueOf(receivedKits));
                        receivedKits.clear();
                        expectedKitCount.set(0);
                    }
                } catch (IOException e) {
                    Logger.error("Failed to write kit: {}", e, name);
                }
                return;
            }
            Logger.info("Syncing config: {}.yml", info[0]);
            try {
                Files.write(MoggedKits.getInstance().getDataPath().resolve(info[0] + ".yml"), data.get());
                syncing.set(true);
                try {
                    if (info[0].equalsIgnoreCase("config")) CM.reload(Config.class);
                    if (info[0].equalsIgnoreCase("messages")) CM.reload(Messages.class);
                } finally {
                    syncing.set(false);
                }
                Logger.success("Config synced: {}.yml", info[0]);
            } catch (IOException e) {
                Logger.error("Failed to write config: {}.yml", e, info[0]);
            }
        });
    }

    private void broadcastFile(String config, File file) {
        Logger.debug("Broadcasting file: {} ({})", config, file.getName());
        try {
            String compressed = CompressionUtil.compressFile(file);
            Logger.debug("Compressed to {} chars, publishing...", String.valueOf(compressed.length()));
            pubSub.broadcast("file-sync", config+"|"+compressed);
            Logger.debug("Broadcast sent for {}", config);
        } catch (IOException e) {
            Logger.error("Failed to compress file for broadcast: {}", e, file.getName());
        }
    }

    private String buildKey(UUID uuid, String kit) {
        return KEY_PREFIX + uuid.toString() + ":" + kit;
    }

    @Override
    public void putCooldown(Player player, String kit, int time) {
        String key = buildKey(player.getUniqueId(), kit);
        long expireAt = System.currentTimeMillis() + (time * 60_000L);
        commands.set(key, String.valueOf(expireAt))
                .thenCompose(result -> commands.pexpireat(key, expireAt + 60_000L))
                .exceptionally(ex -> {
                    Logger.error("Failed to set cooldown for {} kit {}", (Exception) ex, player.getName(), kit);
                    return null;
                });
    }

    @Override
    public long getCooldown(Player player, String kit) {
        String key = buildKey(player.getUniqueId(), kit);
        try {
            String value = commands.get(key).get(2, TimeUnit.SECONDS);
            if (value == null) return 0L;
            return Long.parseLong(value);
        } catch (Exception e) {
            Logger.error("Failed to get cooldown for " + player.getName() + " kit " + kit, e);
            return 0L;
        }
    }

    @Override
    public Info getInfo() {
        boolean connected = connection != null && connection.isOpen();
        RedisInfo redis = new RedisInfo(
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.isSsl(),
                instanceId
        );
        return new Info("Redis", connected, -1, redis);
    }

    public void shutdown() {
        if (pubSub != null) pubSub.shutdown();
        if (connection != null) connection.close();
        if (client != null) client.shutdown();
        Logger.info("Redis storage disconnected");
    }
}
