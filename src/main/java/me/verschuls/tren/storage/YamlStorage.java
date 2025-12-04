package me.verschuls.tren.storage;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import me.verschuls.cbu.CFilter;
import me.verschuls.cbu.CIdentifier;
import me.verschuls.cbu.CMI;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
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
            this.storage = new CMI<>(plugin.getDataPath().resolve("player_data"), PlayerData.class, CIdentifier.fileNameUUID(), CFilter.none(), executor);
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
    public long getCooldown(Player player, String kit) {
        UUID uuid = player.getUniqueId();
        PlayerData data = storage.get(uuid).orElseGet(()->{
            PlayerData new_data = storage.create(uuid, uuid.toString());
            new_data.cooldown.computeIfAbsent(kit, (e)->0L);
            storage.save(uuid, new_data);
            return new_data;
        });
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
