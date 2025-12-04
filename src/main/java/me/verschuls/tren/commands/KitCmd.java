package me.verschuls.tren.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.verschuls.cbu.CM;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.utils.Logger;
import me.verschuls.tren.utils.MsgUtils;
import me.verschuls.tren.utils.TextUtils;
import me.verschuls.tren.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class KitCmd implements BasicCommand {

    private KitManager manager;

    public KitCmd() {
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
        if (args.length == 0) {
            manager.openMainMenu(p);
            return;
        }
        if (manager.kitList(p).contains(args[0])) {
            Placeholder.get().setTemp("kit", p, args[0]);
            if (manager.hasCooldown(p, args[0].toLowerCase())) {
                MsgUtils.send(p, msg.getKits().getOn_cooldown().replace("%time%", manager.getCooldown(p, args[0].toLowerCase())));
                return;
            }
            int res = manager.give(p, args[0].toLowerCase());
            Placeholder.get().setTemp("dropped", p, String.valueOf(res));
            if (res == 0) MsgUtils.send(p, msg.getKits().getKit_granted());
            else if (res > 0) MsgUtils.send(p, msg.getKits().getInventory_full_dropped());
            else MsgUtils.send(p, msg.getKits().getInventory_full());
            Placeholder.get().cleanTemp(p, "kit", "dropped");
            return;
        }
        MsgUtils.send(p, msg.getKits().getAvailable_kits());
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack css, String @NotNull [] args) {
        if (!(css.getSender() instanceof Player p)) return List.of();
        List<String> suggestions = new ArrayList<>();
        List<String> comp = new ArrayList<>(manager.kitList(p));
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
