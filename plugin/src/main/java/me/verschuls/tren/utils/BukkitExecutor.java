package me.verschuls.tren.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class BukkitExecutor {
    private BukkitExecutor() {}

    public static class Sync implements Executor {
        private final JavaPlugin plugin;
        private final BukkitScheduler scheduler;

        public Sync(JavaPlugin plugin) {
            this.plugin = plugin;
            this.scheduler = plugin.getServer().getScheduler();
        }

        @Override
        public void execute(@NotNull Runnable command) {
            scheduler.runTask(plugin, command);
        }
    }

    public static class ASync implements Executor {
        private final JavaPlugin plugin;
        private final BukkitScheduler scheduler;

        public ASync(JavaPlugin plugin) {
            this.plugin = plugin;
            this.scheduler = plugin.getServer().getScheduler();
        }

        @Override
        public void execute(@NotNull Runnable command) {
            scheduler.runTaskAsynchronously(plugin, command);
        }
    }
}