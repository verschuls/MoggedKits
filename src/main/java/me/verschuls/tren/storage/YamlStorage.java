package me.verschuls.tren.storage;

import de.exlll.configlib.Configuration;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.utils.Logger;
import me.verschuls.ylf.CFilter;
import me.verschuls.ylf.CIdentifier;
import me.verschuls.ylf.CMI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class YamlStorage extends StorageHandler {

    private final CMI<UUID, PlayerData> storage;

    public YamlStorage(JavaPlugin plugin, Executor executor) {
        super(plugin, executor);
        Logger.info("Initiating YAML storage...");
        try {
            this.storage = CMI.newBuilder(plugin.getDataPath().resolve("player_data"), PlayerData.class, CIdentifier.fileNameUUID()).filter(CFilter.none()).executor(executor).build();
            this.storage.onInit().thenAcceptAsync(playerData -> {
                Logger.success("YAML storage loaded");
            }, executor).exceptionallyAsync(throwable -> {
                Logger.error("There was an issue with creating YAML storage!", new Exception(throwable));
                MoggedKits.disable();
                return null;
            }, executor);
        } catch (IOException e) {
            Logger.error("There was an issue with creating YAML storage", e);
            MoggedKits.disable();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putCooldown(Player player, String kit, int time) {
        UUID uuid = player.getUniqueId();
        PlayerData data = storage.get(uuid).orElseGet(()->storage.create(uuid, uuid.toString()));
        data.cooldown.put(kit, System.currentTimeMillis()+(time*60_000L));
        storage.save(uuid, data);
    }

    @Override
    public Long getCooldown(Player player, String kit) {
        UUID uuid = player.getUniqueId();
        PlayerData data = storage.get(uuid).orElseGet(()->{
            PlayerData new_data = storage.create(uuid, uuid.toString());
            new_data.cooldown.putIfAbsent(kit, 0L);
            storage.save(uuid, new_data);
            return new_data;
        });
        if (!data.cooldown.containsKey(kit)) {
            data.cooldown.put(kit, 0L);
            storage.save(uuid, data);
        }
        return data.cooldown.get(kit);
    }

    @Override
    public Info getInfo() {
        return new Info("YAML", true, storage.get().size(), null);
    }

    @Configuration
    private static class PlayerData {
        private Map<String, Long> cooldown = new LinkedHashMap<>();
    }
}
