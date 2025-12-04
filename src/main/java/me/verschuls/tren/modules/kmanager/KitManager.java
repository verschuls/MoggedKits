package me.verschuls.tren.modules.kmanager;

import me.verschuls.cbu.CFilter;
import me.verschuls.cbu.CIdentifier;
import me.verschuls.cbu.CM;
import me.verschuls.cbu.CMI;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.config.YamlGUI;
import me.verschuls.tren.config.kits.YamlKit;
import me.verschuls.tren.modules.gui.GUI;
import me.verschuls.tren.modules.gui.GUIManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.utils.ItemUtils;
import me.verschuls.tren.utils.Logger;
import me.verschuls.tren.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KitManager {

    private static final KitManager instance = new KitManager();

    private static final CompletableFuture<KitManager> waiter = new CompletableFuture<>();
    private static final List<Consumer<KitManager>> reload = new CopyOnWriteArrayList<>();

    public static KitManager get() {
        return instance;
    }

    private JavaPlugin plugin;

    private CMI<String, YamlKit> yamlKits;

    private String defaultKit;

    private final Map<String, Kit> kits = new ConcurrentHashMap<>();


    private KitManager() {
        MoggedKits.whenEnabled().thenAcceptAsync(plugin -> {
            CM.onInit(Config.class).thenAcceptAsync(config -> {
                this.plugin = plugin;
                try {
                    Logger.info("Initializing kits...");
                    Path kitsDir = plugin.getDataPath().resolve("kits");
                    if (Files.notExists(kitsDir)) Files.createDirectory(kitsDir);
                    createFromResource("chad.yml", kitsDir, config.getDefaultKit() + ".yml");
                    createFromResource("_example_.yml", kitsDir, "_example_.yml");
                    this.defaultKit = config.getDefaultKit();
                    this.yamlKits = new CMI<>(plugin.getDataPath().resolve("kits"), YamlKit.class, ((file, file_) -> file.getName().replaceFirst("\\.(yml|yaml)$", "").toLowerCase()), CFilter.underScores(), MoggedKits.getExecutor());
                    this.yamlKits.onInit().thenAcceptAsync(kit -> {
                        loadKits(kit);
                        Logger.success("Kits loaded! &2" + kit.keySet());
                        Placeholder.get().registerComplex("kit", (player, args) -> {
                            if (args.length <= 1) return "KitFunctionNotFound";
                            return switch (args[1]) {
                                case "cooldown" -> getCooldown(player, args[0]);
                                default -> "KitFunctionNotFound";
                            };
                        });
                        Placeholder.get().registerSimple("kits", (player -> Utils.clean(kitList(player))));
                        waiter.completeAsync(KitManager::get, MoggedKits.getExecutor());
                    }, MoggedKits.getExecutor()).exceptionallyAsync(throwable -> {
                        Logger.error("There was an issue while creating/reading kits", new Exception(throwable));
                        MoggedKits.disable();
                        return null;
                    }, MoggedKits.getExecutor());
                } catch (IOException e) {
                    Logger.error("There was an issue while initializing kits", e);
                    throw new RuntimeException();
                }
                this.yamlKits.onReload(this::loadKits);
            }, MoggedKits.getExecutor());
        }, MoggedKits.getExecutor());
    }

    public static CompletableFuture<KitManager> whenInitialized() {
        return waiter;
    }

    public static void onReload(Consumer<KitManager> consumer) {
        reload.add(consumer);
    }

    private void createFromResource(String res, Path path, String actual) {
        try (InputStream stream = plugin.getResource(res)) {
            Path file = path.resolve(actual);
            if (Files.notExists(file)) Files.copy(stream, file);
        } catch (IOException e) {
            Logger.error("There was an issue while initializing kits", e);
            throw new RuntimeException();
        }
    }

    private void loadKits(HashMap<String, YamlKit> kits) {
        PluginManager manager = this.plugin.getServer().getPluginManager();
        for (Permission perm : manager.getPermissions())
            if (perm.getName().startsWith("moggedkits.kit."))
                manager.removePermission(perm);
        this.kits.clear();
        YamlGUI.Menu yamlGUI = CM.get(Config.class).getMain_menu();
        GUI.Builder main_menu = GUI.newBuilder(yamlGUI.getTitle(), yamlGUI.getRows(), "main_menu_kits");
        for (int i = 0; i < yamlGUI.getRows()*9; i++)
            main_menu.setItem(i, ItemUtils.blankItem(Material.valueOf(yamlGUI.getFiller().toUpperCase())));
        kits.forEach((key, kit)->{
            int place = kit.getWeight();
            this.kits.put(key, new Kit(key, kit));
            YamlKit.Display display = kit.getDisplay();
            if (!key.equalsIgnoreCase(defaultKit)) {
                manager.addPermission(Permission.loadPermission("moggedkits.kit." + key, Map.of("default", "op")));
                main_menu.render(yamlGUI.getKit_slots()[place], (p, id) -> {
                    if (p.hasPermission("moggedkits.kit." + key)) {
                        if (hasCooldown(p, key)) return display.getCooldown().format(p);
                        return display.getAccess().format(p);
                    }
                    return display.getDenied().format(p);
                });
            } else main_menu.render(yamlGUI.getKit_slots()[place], (p, id) -> {
                if (hasCooldown(p, key)) return display.getCooldown().format(p);
                return display.getAccess().format(p);
            });
            main_menu.button(yamlGUI.getKit_slots()[place], (event)->{
                if (event.getClickType().isLeftClick()) {
                    event.getPlayer().performCommand("kit "+key);
                    event.getPlayer().closeInventory();
                }
                if (event.getClickType().isRightClick()) preview(event.getPlayer(), key);
                return null;
            });
        });
        GUIManager.get().register(main_menu.build());
    }

    public void reload() {
        this.yamlKits.reload();
        reload.forEach(this::accept);
    }

    public Set<String> kitList() {
        return yamlKits.get().keySet();
    }

    public Set<String> kitList(Player player) {
        return yamlKits.get().keySet().stream().filter(k -> player.hasPermission("moggedkits.kit."+k) || defaultKit.equalsIgnoreCase(k)).collect(Collectors.toSet());
    }

    public String getCooldown(Player player, String kit) {
        long seconds = (MoggedKits.getStorage().getCooldown(player, kit)-System.currentTimeMillis()) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder sb = new StringBuilder();

        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public boolean hasCooldown(Player player, String kit) {
        return MoggedKits.getStorage().getCooldown(player, kit)-System.currentTimeMillis() > 0;
    }

    public void openMainMenu(Player player) {
        GUIManager.get().open(player, "main_menu_kits");
    }

    public int give(Player player, String kit) {
        player.updateInventory();
        int res = kits.get(kit).give(player);
        player.updateInventory();
        if (res >= 0) {
            if (!player.hasPermission("moggedkits.admin"))
                MoggedKits.getStorage().putCooldown(player, kit, yamlKits.get(kit).get().getCooldown());
            return res;
        }
        return -1;
    }

    public void preview(Player player, String kit) {
        kits.get(kit).preview(player);
    }

    private void accept(Consumer<KitManager> c) {
        c.accept(this);
    }
}
