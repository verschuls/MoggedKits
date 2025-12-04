package me.verschuls.tren;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.verschuls.cbu.CM;
import me.verschuls.tren.commands.KitCmd;
import me.verschuls.tren.commands.KitsCmd;
import me.verschuls.tren.commands.MoggedCmd;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.config.Redis;
import me.verschuls.tren.modules.gui.GUI;
import me.verschuls.tren.modules.gui.GUIManager;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.storage.RedisStorage;
import me.verschuls.tren.storage.StorageHandler;
import me.verschuls.tren.storage.YamlStorage;
import me.verschuls.tren.utils.BukkitExecutor;
import me.verschuls.tren.utils.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public final class MoggedKits extends JavaPlugin {

    @Getter
    private static MoggedKits instance;

    @Getter
    private static BukkitExecutor executor;

    @Getter
    private static StorageHandler storage;

    private static final CompletableFuture<MoggedKits> waiter = new CompletableFuture<>();

    @Getter
    private static boolean packets = false;

    @Override
    public void onLoad() {
        instance = this;
        executor = new BukkitExecutor(this);
        Placeholder.get();
        if (getServer().getPluginManager().getPlugin("packetevents") != null) {
            Logger.info("Found PacketEvents GUI's will now use more secured option");
            //packets = true;
            //PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
            //PacketEvents.getAPI().load();
        }
    }

    @Override
    public void onEnable() {
        if (packets) PacketEvents.getAPI().init();
        waiter.completeAsync(MoggedKits::getInstance, executor);
        CM.register(new Config(getDataPath(), getExecutor()));
        GUIManager.get();
        KitManager.get();
        CM.register(new Messages(getDataPath(), getExecutor()));
        CM.onInit(Messages.class).thenAcceptAsync(data -> {
            Placeholder.get().registerStatic("prefix", data.getPrefix());
        }, executor);
        CM.onReload(Messages.class, (data)->{
            Placeholder.get().registerStatic("prefix", data.getPrefix());
        });
        CM.register(new Redis(getDataPath(), getExecutor()));
        CM.onInit(Redis.class).thenAcceptAsync(redis -> {
           if (!redis.getHost().isBlank() && redis.getPort() > -1)
               storage = new RedisStorage(instance, executor, redis);
           else storage = new YamlStorage(instance, executor);
        }, executor);
        registerCommand("kit", new KitCmd());
        registerCommand("kits", new KitsCmd());
        registerCommand("moggedkits", List.of("mogged", "mk6"), new MoggedCmd());
        if (packets) PacketEvents.getAPI().getEventManager().registerListener(GUIManager.get(), PacketListenerPriority.HIGH);
        else getServer().getPluginManager().registerEvents(GUIManager.get(), this);
    }

    public static CompletableFuture<MoggedKits> whenEnabled() {
        return waiter;
    }

    @Override
    public void onDisable() {
        if (packets) PacketEvents.getAPI().terminate();
        if (storage instanceof RedisStorage redisStorage) redisStorage.shutdown();
    }

    public static void disable() {
        Logger.warn("Plugin will be disabled");
        MoggedKits.getInstance().getServer().getPluginManager().disablePlugin(MoggedKits.getInstance());
    }
}
