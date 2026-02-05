package me.verschuls.tren;

import lombok.Getter;
import me.verschuls.mkapi.MoggedKitsAPI;
import me.verschuls.tren.commands.BuyKitCmd;
import me.verschuls.tren.commands.KitCmd;
import me.verschuls.tren.commands.KitsCmd;
import me.verschuls.tren.commands.MoggedCmd;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.config.Redis;
import me.verschuls.tren.modules.gui.GUIManager;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.storage.H2Storage;
import me.verschuls.tren.storage.RedisStorage;
import me.verschuls.tren.storage.StorageHandler;
import me.verschuls.tren.utils.BukkitExecutor;
import me.verschuls.tren.utils.Logger;
import me.verschuls.ylf.CM;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Event;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public final class MoggedKits extends JavaPlugin {

    @Getter
    private static MoggedKits instance;

    @Getter
    private static Executor executor;

    @Getter
    private static Executor executorAsync;

    @Getter
    private static StorageHandler storage;

    @Getter
    private static boolean vault = false;

    @Getter
    private static Economy economy;

    private static final CompletableFuture<MoggedKits> waiter = new CompletableFuture<>();

    @Override
    public void onLoad() {
        instance = this;
        executor = new BukkitExecutor.Sync(this);
        executorAsync = new BukkitExecutor.ASync(this);
    }

    @Override
    public void onEnable() {
        Placeholder.get();
        Logger.info("Looking for &3Vault...");
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                vault = true;
                Logger.success("Successfully hooked into &2Vault");
            } else Logger.error("Wasn't able to hook into &4Vault");
        } else Logger.warn("No vault instance found. Kits will have disabled prices");
        waiter.completeAsync(MoggedKits::getInstance, executor);
        CM.register(new Config(getDataPath(), executor));
        GUIManager.get();
        KitManager.get();
        CM.register(new Messages(getDataPath(), executor));
        CM.onInit(Messages.class).thenAcceptAsync(data -> {
            Placeholder.get().registerStatic("prefix", data.getPrefix());
        }, executor);
        CM.onReload(Messages.class, (data)->{
            Placeholder.get().registerStatic("prefix", data.getPrefix());
        });
        CM.register(new Redis(getDataPath(), executor));
        CM.onInit(Redis.class).thenAcceptAsync(redis -> {
           if (!redis.getHost().isBlank() && redis.getPort() > -1)
               storage = new RedisStorage(instance, executorAsync, redis);
           else storage = new H2Storage(instance, executorAsync);
        }, executor);
        if (isVault()) registerCommand("buykit", new BuyKitCmd());
        registerCommand("kit", new KitCmd());
        registerCommand("kits", new KitsCmd());
        registerCommand("moggedkits", List.of("mks"), new MoggedCmd());
        getServer().getPluginManager().registerEvents(GUIManager.get(), this);
        MoggedKitsAPI.set(new MKProvider());
    }

    public static CompletableFuture<MoggedKits> whenEnabled() {
        return waiter;
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.shutdown();
    }

    public static void disable() {
        Logger.warn("Plugin will be disabled");
        MoggedKits.getInstance().getServer().getPluginManager().disablePlugin(MoggedKits.getInstance());
    }

    public static <T extends Event> CompletableFuture<T> callEvent(T event) {
        if (getInstance().getServer().isPrimaryThread()) {
            getInstance().getServer().getPluginManager().callEvent(event);
            return CompletableFuture.completedFuture(event);
        }
        return CompletableFuture.supplyAsync(() -> {
            getInstance().getServer().getPluginManager().callEvent(event);
            return event;
        }, executor);
    }
}
