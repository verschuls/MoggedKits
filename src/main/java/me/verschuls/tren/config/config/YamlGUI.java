package me.verschuls.tren.config.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import me.verschuls.tren.config.minecraft.YamlItemStack;

@Getter
public class YamlGUI {

    private YamlGUI() {}

    @Configuration @Getter
    public static class Kit {

        @Comment("Material for empty slots - leave blank if you like ugly GUIs")
        private String filler = "GRAY_STAINED_GLASS_PANE";

        @Comment("Where your loot goes (max 36 slots, empties become air)")
        private int items_slot_start = 0;
        private int items_slot_end = 35;

        @Comment("Armor display slots (4 slots: helmet -> boots, left to right)")
        private int[] armor_slots = {45, 46, 47, 48};

        @Comment("The 'claim this shit' button in preview")
        private StatusIcon statusIcon = new StatusIcon();

        @Configuration @Getter
        public static class StatusIcon {
            private boolean enabled = true;
            private int slot = 51;

            @Comment("true = custom materials below, false = yoinks the kit's icon")
            private boolean changedIcon = true;

            @Comment("You're worthy - grab it king")
            private String access = "GREEN_DYE";

            @Comment("Skill issue - no perms")
            private String denied = "RED_DYE";

            @Comment("Patience grasshopper - still on cooldown")
            private String cooldown = "YELLOW_DYE";
        }

        private int return_slot = 53;
        private YamlItemStack.Basic return_item = YamlItemStack.basic("&cGo Back", "ARROW");
    }

    @Configuration @Getter
    public static class Menu {
        private String title = "&8» &c&lMOG &8or &c&lBE MOGGED &8«";

        @Comment("1-6 rows (9-54 slots) - bigger isn't always better")
        private int rows = 3;

        @Comment("Material for empty slots - leave blank if you like ugly GUIs")
        private String filler = "GRAY_STAINED_GLASS_PANE";

        @Comment("Where kit icons spawn - plan this or it looks like ass")
        private int[] kit_slots = {13};
    }
}
