package me.verschuls.tren.config;

import de.exlll.configlib.Comment;
import lombok.Getter;
import me.verschuls.cbu.BaseConfig;
import me.verschuls.cbu.Header;
import me.verschuls.tren.config.config.YamlGUI;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Config extends BaseConfig<Config.Data> {

    public Config(Path path, Executor executor) {
        super(path, "config", Data.class, executor);
    }

    @Getter
    @Header("""
                __  ___                           ____ __ _ __     \s
               /  |/  /___  ____ _____ ____  ____/ / //_/(_) /______
              / /|_/ / __ \\/ __ `/ __ `/ _ \\/ __  / ,<  / / __/ ___/
             / /  / / /_/ / /_/ / /_/ /  __/ /_/ / /| |/ / /_(__  )\s
            /_/  /_/\\____/\\__, /\\__, /\\___/\\__,_/_/ |_/_/\\__/____/ \s
                         /____//____/                              \s
            
            
            """)
    public static class Data extends BaseConfig.Data {


        @Comment("Used for reporting bugs and editing if u know what your doing")
        private boolean debug = false;

        @Comment("Config version - touch this and your config gets mogged on next reload")
        private Double version = 1.0;

        @Comment({
            "The unkillable kit. Delete it? It respawns. Like a cockroach but useful.",
            "This kit requires no permission - even the most beta players deserve something."
        })
        private String defaultKit = "chad";

        @Comment({
            "When your inventory is already stuffed like a Thanksgiving turkey:",
            "true = items get yeeted on the ground (sigma move)",
            "false = blocked until you clean up your mess like your mom told you to"
        })
        private boolean dropWhenFull = false;

        @Comment({
            "The main menu that greets players when they /kits or /kit",
            "Make it pretty or don't, I'm a config file not a cop"
        })
        private YamlGUI.Menu main_menu = new YamlGUI.Menu();

        @Comment({
            "Universal kit preview - shows what you're about to claim",
            "Hardcoded to 54 slots because you need room for a full loadout",
            "No I won't add size config. Touch grass instead of asking for features."
        })
        private YamlGUI.Kit kit_preview = new YamlGUI.Kit();
    }
}
