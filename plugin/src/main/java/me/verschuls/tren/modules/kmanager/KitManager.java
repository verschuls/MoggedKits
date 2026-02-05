package me.verschuls.tren.modules.kmanager;

import me.verschuls.mkapi.KitsLoadedEvent;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.config.YamlGUI;
import me.verschuls.tren.config.kits.YamlKit;
import me.verschuls.tren.modules.gui.GUI;
import me.verschuls.tren.modules.gui.GUIManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.utils.ItemUtils;
import me.verschuls.tren.utils.Logger;
import me.verschuls.tren.utils.MsgUtils;
import me.verschuls.tren.utils.Utils;
import me.verschuls.ylf.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private final Map<String, String[]> deniedActions = new HashMap<>();

    private String defaultKit;

    private final Map<String, Kit> kits = new ConcurrentHashMap<>();


    private KitManager() {
        MoggedKits.whenEnabled().thenAcceptAsync(plugin -> {
            CM.onInit(Config.class).thenAcceptAsync(config -> {
                this.plugin = plugin;
                try {
                    Logger.info("Initializing kits...");
                    Path kitsDir = plugin.getDataPath().resolve("kits");
                    if (Files.notExists(kitsDir)) {
                        Logger.debug("Creating kits directory: " + kitsDir);
                        Files.createDirectory(kitsDir);
                    }
                    Logger.debug("Loading default kit resource: " + config.getDefaultKit() + ".yml");
                    createFromResource("chad.yml", kitsDir, config.getDefaultKit() + ".yml");
                    createFromResource("mogger.yml", kitsDir, "mogger.yml");
                    createFromResource("_example_.yml", kitsDir, "_example_.yml");
                    this.defaultKit = config.getDefaultKit();
                    Logger.debug("Default kit set to: " + this.defaultKit);
                    Logger.debug("Building CMI for kits directory...");
                    this.yamlKits = CMI.newBuilder(kitsDir, YamlKit.class, CIdentifier.fileName()).filter(CFilter.underScores()).executor(MoggedKits.getExecutor()).build();
                    Logger.debug("CMI built for kits directory...");
                    this.yamlKits.onInit().thenAcceptAsync(kit -> {
                        Logger.debug("CMI initialized, found " + kit.size() + " kit files");
                        loadKits(kit);
                        Logger.success("Kits loaded! &2" + kit.keySet());
                        Placeholder.get().registerComplex("kit", (player, args) -> {
                            if (args.length <= 1) return "KitFunctionNotFound";
                            return String.valueOf(switch (args[1]) {
                                case "cooldown" -> getCooldown(player, args[0]);
                                case "rawcooldown" -> getRawCooldown(player, args[0]);
                                case "title" -> kit.get(args[0]).getData().getGuiTitle();
                                case "access" -> hasAccess(player, args[0]);
                                default -> "KitFunctionNotFound";
                            });
                        });
                        Placeholder.get().registerSimple("kits", (player -> Utils.clean(kitList(player))));
                        waiter.complete(this);
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
            if (Files.notExists(file)) {
                Logger.debug("Creating kit file from resource: " + res + " -> " + actual);
                Files.copy(stream, file);
            }
        } catch (IOException e) {
            Logger.error("There was an issue while initializing kits", e);
            throw new RuntimeException();
        }
    }

    private void loadKits(HashMap<String, ConfigInfo<YamlKit>> kits) {
        Logger.debug("Starting kit loading process...");
        PluginManager manager = this.plugin.getServer().getPluginManager();
        Logger.debug("Cleaning up old kit permissions...");
        for (Permission perm : manager.getPermissions())
            if (perm.getName().startsWith("moggedkits.kit."))
                manager.removePermission(perm);
        this.kits.clear();
        this.deniedActions.clear();
        Logger.debug("Loading main menu configuration...");
        YamlGUI.Menu yamlGUI = CM.get(Config.class).getMain_menu();
        GUI.Builder main_menu = GUI.newBuilder(yamlGUI.getTitle(), yamlGUI.getRows(), "main_menu_kits");
        for (int i = 0; i < yamlGUI.getRows()*9; i++)
            main_menu.setItem(i, ItemUtils.blankItem(Material.valueOf(yamlGUI.getFiller().toUpperCase())));
        if (yamlGUI.getClickSound().enabled())
            main_menu.clickSound(yamlGUI.getClickSound().sound(), yamlGUI.getClickSound().volume());
        Logger.debug("Processing " + kits.size() + " kits...");
        kits.forEach((key, kit_)->{
            Logger.debug("Loading kit: " + key);
            YamlKit kit = kit_.getData();
            if (kit.getLmb_denied_behavior().isEnabled()) {
                deniedActions.put(key, kit.getLmb_denied_behavior().getActions());
                Logger.debug("  - Registered denied actions for: " + key);
            }
            int place = kit.getSlot();
            this.kits.put(key, new Kit(key, kit, yamlGUI.getClickSound()));
            Logger.debug("  - Kit registered at slot: " + place);
            YamlKit.Display display = kit.getDisplay();
            if (!key.equalsIgnoreCase(defaultKit)) {
                manager.addPermission(Permission.loadPermission("moggedkits.kit." + key, Map.of("default", "op")));
                Logger.debug("  - Permission registered: moggedkits.kit." + key);
                main_menu.render(place, (p, id) -> {
                    if (hasAccess(p, key)) {
                        if (hasCooldown(p, key)) return display.getCooldown().format(p);
                        return display.getAccess().format(p);
                    }
                    if (MoggedKits.isVault() && kit.getPrice() > 0) return display.getBuy().format(p);
                    return display.getDenied().format(p);
                });
            } else main_menu.render(place, (p, id) -> {
                if (hasCooldown(p, key)) return display.getCooldown().format(p);
                return display.getAccess().format(p);
            });
            main_menu.button(place, (event)->{
                if (event.getClickType().isLeftClick()) {
                    event.getPlayer().performCommand("kit "+key);
                    event.getPlayer().closeInventory();
                }
                if (event.getClickType().isRightClick()) preview(event.getPlayer(), key);
                return null;
            });
        });
        Logger.debug("Registering main menu GUI...");
        GUIManager.get().register(main_menu.build());
        Logger.debug("Firing KitsLoadedEvent with " + kits.size() + " kits");
        MoggedKits.callEvent(new KitsLoadedEvent(new ArrayList<>(kitListFull().stream().map(Kit::intoAPI).toList())));
    }

    public void reload() {
        Logger.debug("Reloading kits...");
        this.yamlKits.reload();
        Logger.debug("Notifying " + reload.size() + " reload listeners");
        reload.forEach(this::accept);
    }

    public Set<String> kitList() {
        return yamlKits.get().keySet();
    }

    public Set<Kit> kitListFull() {
        return Set.copyOf(kits.values());
    }

    public Set<String> kitList(Player player) {
        return yamlKits.get().keySet().stream().filter(k -> hasAccess(player, k)).collect(Collectors.toSet());
    }

    public void setAccess(Player player, String kit, boolean access) {
        if (kit.equalsIgnoreCase("all")) {
            kitList().forEach(k->MoggedKits.getStorage().setAccess(player, k, access));
            return;
        }
        MoggedKits.getStorage().setAccess(player, kit, access);
    }

    public boolean hasAccess(Player player, String kit) {
        return player.hasPermission("moggedkits.kit."+kit) || MoggedKits.getStorage().hasAccess(player, kit) || defaultKit.equalsIgnoreCase(kit);
    }

    long getRawCooldown(String kit) {
        return yamlKits.get(kit).get().getCooldown();
    }

    public long getRawCooldown(Player player, String kit) {
        long cooldown = MoggedKits.getStorage().getCooldown(player, kit)-System.currentTimeMillis();
        return cooldown <= 0 ? 0 : cooldown;
    }

    public String getCooldown(Player player, String kit) {
        long cooldown = getRawCooldown(player, kit);
        if (cooldown == 0) return "0";

        long seconds = cooldown / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        List<String> parts = new ArrayList<>(4);
        if (days > 0) parts.add(days + "d");
        if (hours > 0) parts.add(hours + "h");
        if (minutes > 0) parts.add(minutes + "m");
        if (seconds > 0 || parts.isEmpty()) parts.add(seconds + "s");

        return String.join(" ", parts.subList(0, Math.min(2, parts.size())));
    }

    public double kitPrice(String kit) {
        return kits.get(kit).getPrice();
    }

    public boolean runDenied(Player player, String kit) {
        if (!deniedActions.containsKey(kit)) return false;
        for (String action : deniedActions.get(kit)) {
            if (action.startsWith("/")) player.performCommand(action.replaceFirst("/", ""));
            else MsgUtils.send(player, action);
        }
        return true;
    }

    public boolean hasCooldown(Player player, String kit) {
        return MoggedKits.getStorage().getCooldown(player, kit)-System.currentTimeMillis() > 0;
    }

    public void resetCooldown(Player player, String kit) {
        if (kit.equalsIgnoreCase("ALL")) {
            for (String kit_ : kitList())
                MoggedKits.getStorage().putCooldown(player, kit_, 0);
            return;
        }
        MoggedKits.getStorage().putCooldown(player, kit, 0);
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
        return res;
    }

    public Optional<Kit> getKit(String name) {
        return Optional.ofNullable(kits.get(name));
    }

    public void preview(Player player, String kit) {
        kits.get(kit).preview(player);
    }

    private void accept(Consumer<KitManager> c) {
        c.accept(this);
    }
}
