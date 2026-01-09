package me.verschuls.tren.config.kits;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;
import me.verschuls.tren.config.minecraft.YamlItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Getter @Setter
public class YamlKit {

    private Integer cooldown = 5;
    private Integer weight = -1;
    private Integer slot = 13;
    private String guiTitle = "";

    private Display display = new Display();

    @Configuration
    @Getter
    public static class Display {
        private YamlItemStack.Basic access = YamlItemStack.basic();
        private YamlItemStack.Basic denied = YamlItemStack.basic();
        private YamlItemStack.Basic cooldown = YamlItemStack.basic();
    }

    private StatusIcon statusIcon = new StatusIcon();

    @Configuration @Getter
    public static class StatusIcon {
        private boolean enabled = true;
        private int slot = 51;
        private Type modify_type = Type.OVERRIDE_PARTS;
        private YamlItemStack.Basic access = YamlItemStack.basic("&7", "GREEN_DYE");
        private YamlItemStack.Basic denied = YamlItemStack.basic("&7", "RED_DYE");
        private YamlItemStack.Basic cooldown = YamlItemStack.basic("&7", "YELLOW_DYE");

        public enum Type {
            OVERRIDE_PARTS,
            OVERRIDE_WHOLE,
            NONE;
        }
    }


    private DeniedBehavior lmb_denied_behavior = new DeniedBehavior();

    @Configuration
    @Getter
    public static class DeniedBehavior {
        private boolean enabled = false;
        private String[] actions = {};
    }

    private Armor armor = new Armor();

    @Configuration
    @Getter
    public static class Armor {
        private boolean autoEquip = true;
        private YamlItemStack.Basic helmet = YamlItemStack.basic();
        private YamlItemStack.Basic chestplate = YamlItemStack.basic();
        private YamlItemStack.Basic leggings = YamlItemStack.basic();
        private YamlItemStack.Basic boots = YamlItemStack.basic();
    }


    private Map<String, YamlItemStack.Section> items = new LinkedHashMap<>(Map.of("STONE", YamlItemStack.section()));
}
