package me.verschuls.tren.utils;

import me.verschuls.tren.modules.placeholder.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MsgUtils {


    public static void send(CommandSender sender, String message) {
        sender.sendMessage(TextUtils.format(Placeholder.get().parse(sender instanceof Player p ? p : null, message)));
    }

    public static void send(CommandSender sender, Player context, String message) {
        sender.sendMessage(TextUtils.format(Placeholder.get().parse(context, message)));
    }

    public static void send(CommandSender sender, List<String> message) {
        List<String> parsed = message.stream().map(s -> Placeholder.get().parse(sender instanceof Player p ? p : null, s)).toList();
        sender.sendMessage(TextUtils.format(parsed));
    }

    public static void send(CommandSender sender, Player context, List<String> message) {
        List<String> parsed = message.stream().map(s -> Placeholder.get().parse(context, s)).toList();
        sender.sendMessage(TextUtils.format(parsed));
    }
}
