package me.verschuls.tren.utils;


import me.verschuls.cbu.CM;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.config.Config;

public class Logger {

    private final static String INFO = "&3[MoggedKits/INFO] &b";
    private final static String SUCCESS = "&2[MoggedKits/SUCCESS] &a";
    private final static String WARN = "&6[MoggedKits/WARN] &e";
    private final static String DEBUG = "&5[MoggedKits/DEBUG] &d";
    private final static String ERROR = "&4[MoggedKits/ERROR] &c";

    private static void msg(String message, String... args) {
        MoggedKits.getInstance().getServer().getConsoleSender().sendMessage(TextUtils.format(Utils.args(message, args)));
    }

    public static void info(String message, String... args) {
        msg(INFO+message, args);
    }

    public static void success(String message, String... args) {
        msg(SUCCESS+message, args);
    }

    public static void warn(String message, String... args) {
        msg(WARN+message, args);
    }

    public static void debug(String message, String... args) {
        if (!CM.get(Config.class).isDebug()) return;
        msg(DEBUG+message, args);
    }

    public static void error(String message, String... args) {
        msg(ERROR+message, args);
    }

    public static void error(String message, Exception e, String... args) {
        msg(ERROR+message, args);
        e.printStackTrace();
    }
}
