package me.verschuls.tren.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.config.messages.MKits;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.utils.Logger;
import me.verschuls.tren.utils.MsgUtils;
import me.verschuls.tren.utils.TextUtils;
import me.verschuls.ylf.CM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class KitCmd implements BasicCommand {

    private KitManager manager;

    public KitCmd() {
        KitManager.whenInitialized().thenAccept(manager -> this.manager = manager);
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
        String kitName = args[0].toLowerCase();
        if (manager.kitList(p).contains(kitName)) {
            Placeholder.get().setTemp("kit", p, kitName);
            if (manager.hasCooldown(p, kitName)) {
                MsgUtils.send(p, msg.getKits().getOn_cooldown().replace("%time%", manager.getCooldown(p, kitName)));
                Placeholder.get().cleanTemp(p, "kit");
                return;
            }
            int res = manager.give(p, kitName);
            if (res == -100) return;
            Placeholder.get().setTemp("dropped", p, String.valueOf(res));
            if (res == 0) MsgUtils.send(p, msg.getKits().getKit_granted());
            else if (res > 0) MsgUtils.send(p, msg.getKits().getInventory_full_dropped());
            else MsgUtils.send(p, msg.getKits().getInventory_full());
            Placeholder.get().cleanTemp(p, "kit", "dropped");
            return;
        }
        if (manager.kitList().contains(kitName)) {
            if (MoggedKits.isVault()) {
                Placeholder.get().setTemp("kit", p, kitName);
                Placeholder.get().setTemp("price", p, manager.kitPrice(kitName)+"");
                MKits.Economy eco = msg.getKits().getEconomy();
                Component confirm = TextUtils.format(eco.getConfirm()).clickEvent(ClickEvent.runCommand("buykit "+kitName));
                p.sendMessage(TextUtils.format(Placeholder.get().parse(p, eco.getBuy())).replaceText(TextReplacementConfig.builder().match("%confirm%").replacement(confirm).build()));
                Placeholder.get().cleanTemp(p, "kit", "price");
                return;
            }
            if (manager.runDenied(p, args[0])) return;
        }
        MsgUtils.send(p, msg.getKits().getAvailable_kits());
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack css, String @NotNull [] args) {
        if (!(css.getSender() instanceof Player p)) return List.of();
        List<String> suggestions = new ArrayList<>();
        List<String> comp = new ArrayList<>(manager.kitList());
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
