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
import me.verschuls.ylf.CM;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BuyKitCmd implements BasicCommand {

    private KitManager manager;

    public BuyKitCmd() {
        KitManager.whenInitialized().thenAccept(manager -> this.manager = manager);
    }

    @Override
    public void execute(CommandSourceStack css, String[] args) {
        if (manager == null) {
            css.getSender().sendMessage("KitManager still initializing...");
            return;
        }
        if (!(css.getSender() instanceof Player p)) {
            Logger.info(CM.get(Messages.class).getCommon().getOnly_player());
            return;
        }
        String kitName = args[0].toLowerCase();
        Placeholder.get().setTemp("kit", p, kitName);
        if (!manager.kitList().contains(kitName)) {
            MsgUtils.send(p, CM.get(Messages.class).getKits().getAvailable_kits());
            Placeholder.get().cleanTemp(p, "kit");
            return;
        }
        MKits.Economy eco = CM.get(Messages.class).getKits().getEconomy();
        Placeholder.get().setTemp("price", p, manager.kitPrice(kitName)+"");
        if (manager.hasAccess(p, kitName)) {
            MsgUtils.send(p, eco.getBought());
            Placeholder.get().cleanTemp(p, "kit", "price");
            return;
        }
        if (!MoggedKits.getEconomy().has(p, manager.kitPrice(kitName))) {
            MsgUtils.send(p, eco.getFailed());
            Placeholder.get().cleanTemp(p, "kit", "price");
            return;
        }
        MoggedKits.getEconomy().withdrawPlayer(p, manager.kitPrice(kitName));
        manager.setAccess(p, kitName, true);
        MsgUtils.send(p, eco.getSuccess());
        Placeholder.get().cleanTemp(p, "kit", "price");
    }

    @Override
    public Collection<String> suggest(CommandSourceStack css, String[] args) {
        return manager.kitList();
    }
}
