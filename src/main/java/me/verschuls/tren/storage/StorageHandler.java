package me.verschuls.tren.storage;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;

public abstract class StorageHandler {
    protected final JavaPlugin plugin;
    protected final Executor executor;
    public StorageHandler(JavaPlugin plugin, Executor executor) {
        this.plugin = plugin;
        this.executor = executor;
    }

    public abstract void putCooldown(Player player, String kit, int time);
    public abstract long getCooldown(Player player, String kit);
    public abstract Info getInfo();


    public record Info(
            String type,
            boolean connected,
            int players,
            RedisInfo redis
    ) {}

    public record RedisInfo(
            String host,
            int port,
            int database,
            boolean ssl,
            String instanceId
    ) {}
}
