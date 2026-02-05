package me.verschuls.tren.config.kits;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;
import me.verschuls.tren.config.minecraft.YamlItemStack;
import me.verschuls.ylf.BaseData;
import me.verschuls.ylf.CVersion;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@CVersion("1.1")
public class YamlKit extends BaseData {

    private Integer cooldown = 5;
    private Double price = 0.0;
    private Integer slot = 13;
    private String guiTitle = "";

    private Display display = new Display();

    @Configuration
    @Getter
    public static class Display {
        private YamlItemStack access = YamlItemStack.create();
        private YamlItemStack denied = YamlItemStack.create();
        private YamlItemStack buy = YamlItemStack.create();
        private YamlItemStack cooldown = YamlItemStack.create();
    }

    private StatusIcon statusIcon = new StatusIcon();

    @Configuration @Getter
    public static class StatusIcon {
        private boolean enabled = true;
        private int slot = 51;
        private Type modify_type = Type.OVERRIDE_PARTS;
        private YamlItemStack access = YamlItemStack.create("&7", "GREEN_DYE");
        private YamlItemStack denied = YamlItemStack.create("&7", "RED_DYE");
        private YamlItemStack cooldown = YamlItemStack.create("&7", "YELLOW_DYE");

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
        private YamlItemStack helmet = YamlItemStack.create();
        private YamlItemStack chestplate = YamlItemStack.create();
        private YamlItemStack leggings = YamlItemStack.create();
        private YamlItemStack boots = YamlItemStack.create();
    }

    private List<YamlItemStack> items = new ArrayList<>(List.of(YamlItemStack.create("STONE")));
}
