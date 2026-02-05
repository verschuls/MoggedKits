package me.verschuls.tren.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.verschuls.tren.config.Messages;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.utils.Logger;
import me.verschuls.ylf.CM;
import org.bukkit.entity.Player;

public class KitsCmd implements BasicCommand {

    private KitManager manager;

    public KitsCmd() {
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
        manager.openMainMenu(p);
    }
}
