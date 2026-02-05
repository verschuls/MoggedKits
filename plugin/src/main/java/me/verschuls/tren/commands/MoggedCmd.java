package me.verschuls.tren.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.storage.H2Storage;
import me.verschuls.tren.storage.StorageHandler;
import me.verschuls.tren.storage.YamlStorage;
import me.verschuls.tren.storage.YamlToH2Converter;
import me.verschuls.tren.utils.MsgUtils;
import me.verschuls.tren.utils.TextUtils;
import me.verschuls.tren.utils.Utils;
import me.verschuls.ylf.CM;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoggedCmd implements BasicCommand {


    private KitManager manager;

    public MoggedCmd() {
        KitManager.whenInitialized().thenAccept(manager -> this.manager = manager);
    }

    @Override
    public void execute(CommandSourceStack css, String[] args) {
        if (manager == null) {
            css.getSender().sendMessage("KitManager still initializing...");
            return;
        }
        Messages.Data msg = CM.get(Messages.class);
        CommandSender s = css.getSender();
        if (!s.hasPermission("moggedkits.admin")) {
            MsgUtils.send(s, msg.getCommon().getNo_permission());
            return;
        }
        if (args.length == 0) {
            MsgUtils.send(s, "%prefix% &eAvailable sub commands: &6reload, storage, resetcooldown, give, removeaccess, addaccess, migrate");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                long start = System.currentTimeMillis();
                CM.reload(Config.class);
                CM.reload(Messages.class);
                manager.reload();
                MsgUtils.send(s, msg.getCommon().getReload().replace("%time%", String.valueOf(System.currentTimeMillis()-start)));
            }
            case "storage" -> {
                StorageHandler.Info info = MoggedKits.getStorage().getInfo();
                List<String> text = new ArrayList<>(List.of("%prefix% &e&lStorage Info",
                        "&7&l- &r&eType: &6"+info.type(), "&7&l- &r&eConnected: "+(info.connected()? "&aMewing" : "&cGot Mogged")));
                if (info.players() >= 0) text.add("&7&l- &r&ePlayers: &6"+info.players());
                else if (info.redis() != null) {
                    StorageHandler.RedisInfo r = info.redis();
                    text.addAll(List.of("&7&l- &r&eHost: &6"+r.host(),
                            "&7&l- &r&eDatabase: &6"+r.database(),
                            "&7&l- &r&eSSL: &6"+r.ssl(),
                            "&7&l- &r&eInstanceID: &6"+r.instanceId()));
                }
                MsgUtils.send(s, text);
            }
            case "resetcooldown" -> {
                if (args.length < 2) {
                    MsgUtils.send(s, "%prefix% &eUsage: &6resetcooldown [kit] (player)");
                    return;
                }
                Player target = validKitPlayer(Utils.multiList(manager.kitList(), "all"), args, msg, s);
                if (target == null) return;
                manager.resetCooldown(target, args[1].toLowerCase());
                MsgUtils.send(s, "%prefix% &eCooldown of &6"+args[1]+" &ereseted for &6"+target.getName());
            }
            case "give" -> {
                if (args.length < 2) {
                    MsgUtils.send(s, "%prefix% &eUsage: &6give [kit] (player)");
                    return;
                }
                Player target = validKitPlayer(manager.kitList(), args, msg, s);
                if (target == null) return;
                int res = manager.give(target, args[1].toLowerCase());
                if (res == -100) {
                    MsgUtils.send(s, "%prefix% &cAction was cancelled by another plugin");
                    return;
                }
                MsgUtils.send(s, "%prefix% &eKit &6"+args[1]+" &egiven to &6"+target.getName());
            }
            case "removeaccess", "addaccess" -> {
                if (args.length < 2) {
                    MsgUtils.send(s, "%prefix% &eUsage: &6setaccess [kit] (player)");
                    return;
                }
                Player target = validKitPlayer(Utils.multiList(manager.kitList(), "all"), args, msg, s);
                if (target == null) return;
                boolean add = args[0].equalsIgnoreCase("addaccess");
                manager.setAccess(target, args[1].toLowerCase(), add);
                MsgUtils.send(s, "%prefix% &eAccess "+(add?"&aadded":"&cremoved")+" &efor &6"+args[1]+" &eto &6"+target.getName());
            }
            case "migrate" -> {
                if (!(MoggedKits.getStorage() instanceof H2Storage h2Storage)) {
                    MsgUtils.send(s, "%prefix% &cMigration only available when using H2 storage");
                    return;
                }
                Path yaml = MoggedKits.getInstance().getDataPath().resolve("player_data");
                if (!Files.exists(yaml)) {
                    MsgUtils.send(s, "%prefix% &cNo YAML data found to migrate");
                    return;
                }
                s.sendMessage(TextUtils.format(Placeholder.get().parse(null, "%prefix% &eMigrate YAML data to H2? "))
                        .append(TextUtils.format("&a&n[CONFIRM]").clickEvent(ClickEvent.callback(a->{
                           MsgUtils.send(s, "%prefix% &eMigrating...");
                            YamlToH2Converter.convert(new YamlStorage(MoggedKits.getInstance(), MoggedKits.getExecutor()), h2Storage).thenAcceptAsync(ms->{
                                MsgUtils.send(s, "%prefix% &aMigration completed in &2"+ms+"ms");
                                Bukkit.getScheduler().runTaskLater(MoggedKits.getInstance(), ()->{
                                    try {
                                        Files.walkFileTree(yaml,
                                                new SimpleFileVisitor<>() {
                                                    @Override
                                                    public FileVisitResult postVisitDirectory(
                                                            Path dir, IOException exc) throws IOException {
                                                        Files.delete(dir);
                                                        return FileVisitResult.CONTINUE;
                                                    }

                                                    @Override
                                                    public FileVisitResult visitFile(
                                                            Path file, BasicFileAttributes attrs)
                                                            throws IOException {
                                                        Files.delete(file);
                                                        return FileVisitResult.CONTINUE;
                                                    }
                                                });
                                    } catch (IOException e) {
                                        MsgUtils.send(s, "%prefix% &cFailed to delete &4player_data &cfolder, remove it manually");
                                    }
                                }, 10L);
                            }, MoggedKits.getExecutor());
                        }))));
            }
            default -> MsgUtils.send(s, "%prefix% &eAvailable sub commands: &6reload, storage, resetcooldown, give, removeaccess, addaccess, migrate");
        }
    }

    private Player validKitPlayer(Collection<String> kitList, String[] args, Messages.Data msg, CommandSender s) {
        if (!kitList.contains(args[1].toLowerCase())) {
            MsgUtils.send(s, "%prefix% &cThis kit doesn't exists!");
            return null;
        }
        Player target = null;
        if (s instanceof Player p) target = p;
        if (args.length == 3) {
            if (Bukkit.getPlayer(args[2]) == null) {
                MsgUtils.send(s, "%prefix% &cPlayer not found!");
                return null;
            }
            target = Bukkit.getPlayer(args[2]);
        }
        if (target == null) {
            MsgUtils.send(s, msg.getCommon().getOnly_player());
            return null;
        }
        return target;
    }

    @Override
    public @Nullable String permission() {
        return "moggedkits.admin";
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack css, String @NotNull [] args) {
        if (!(css.getSender() instanceof Player)) return List.of();
        if (!css.getSender().hasPermission("moggedkits.admin")) return List.of();
        List<String> suggestions = new ArrayList<>();
        List<String> comp = new ArrayList<>(List.of("reload", "storage", "resetcooldown", "give", "migrate", "addaccess", "removeaccess"));
        if (args.length == 0) return comp;
        if (args.length == 1 && !args[0].isBlank()) {
            String input = args[0].toLowerCase();
            for (String s : comp) {
                if (!s.toLowerCase().startsWith(input)) continue;
                suggestions.add(s);
            }
            return suggestions;
        }
        if (args.length == 2 && !args[0].isBlank()) {
            comp.clear();
            comp.addAll(switch (args[0].toLowerCase()) {
                case "resetcooldown", "addaccess", "removeaccess" -> Utils.multiList(manager.kitList(), "ALL");
                case "give" -> manager.kitList();
                default -> List.of();
            });
            if (comp.isEmpty()) return List.of();
            String input = args[1].toLowerCase();
            for (String s : comp) {
                if (!s.toLowerCase().startsWith(input)) continue;
                suggestions.add(s);
            }
            return suggestions;
        }
        if (args.length == 3 && !args[1].isBlank()) {
            comp.clear();
            comp.addAll(switch (args[0].toLowerCase()) {
                case "resetcooldown", "give", "setaccess", "removeaccess" -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                default -> List.of();
            });
            if (comp.isEmpty()) return List.of();
            String input = args[2].toLowerCase();
            for (String s : comp) {
                if (!s.toLowerCase().startsWith(input)) continue;
                suggestions.add(s);
            }
            return suggestions;
        }
        return suggestions;
    }
}
