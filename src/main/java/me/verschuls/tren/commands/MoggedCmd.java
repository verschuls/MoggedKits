package me.verschuls.tren.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.verschuls.cbu.CM;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.storage.StorageHandler;
import me.verschuls.tren.utils.Logger;
import me.verschuls.tren.utils.MsgUtils;
import me.verschuls.tren.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MoggedCmd implements BasicCommand {


    private KitManager manager;

    public MoggedCmd() {
        KitManager.whenInitialized().thenAcceptAsync(manager -> this.manager = manager, MoggedKits.getExecutor());
    }

    @Override
    public void execute(CommandSourceStack css, String[] args) {
        if (manager == null) {
            css.getSender().sendMessage("KitManager still initializing...");
            return;
        }
        Messages.Data msg = CM.get(Messages.class);
        if (!(css.getSender() instanceof Player p)) {
            Logger.info(msg.getCommon().getOnly_player());
            return;
        }
        if (!p.hasPermission("moggedkits.admin")) {
            MsgUtils.send(p, msg.getCommon().getNo_permission());
            return;
        }
        if (args.length == 0) {
            MsgUtils.send(p, "%prefix% &eAvailable sub commands: &6reload, storage");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                long start = System.currentTimeMillis();
                CM.reload(Config.class);
                CM.reload(Messages.class);
                KitManager.get().reload();
                MsgUtils.send(p, msg.getCommon().getReload().replace("%time%", String.valueOf(System.currentTimeMillis()-start)));
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
                MsgUtils.send(p, text);
            }
            default -> MsgUtils.send(p, "%prefix% &eAvailable sub commands: &6reload, storage");
        }
    }

    @Override
    public @Nullable String permission() {
        return "moggedkits.admin";
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack css, String @NotNull [] args) {
        if (!(css.getSender() instanceof Player)) return List.of();
        List<String> suggestions = new ArrayList<>();
        List<String> comp = new ArrayList<>();
        if (css.getSender().hasPermission("moggedkits.admin")) {
            comp.add("reload");
            comp.add("storage");
        }
        if (args.length == 0) return comp;
        if (args.length == 1 && !args[0].isBlank()) {
            String input = args[0].toLowerCase();
            for (String s : comp) {
                if (!s.toLowerCase().startsWith(input)) continue;
                suggestions.add(s);
            }
            return suggestions;
        }
        return suggestions;
    }
}
