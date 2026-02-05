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

        @Comment("Back button slot - 53 is bottom right corner, last slot in a 6-row GUI")
        private int return_slot = 53;

        @Comment("Back button item - returns to main menu, escape route for window shoppers")
        private YamlItemStack return_item = YamlItemStack.create("&cGo Back", "ARROW");
    }

    @Configuration @Getter
    public static class Menu {
        @Comment("Menu title - flex your server's aura here")
        private String title = "&8» &c&lMOG &8or &c&lBE MOGGED &8«";

        @Comment("1-6 rows (9-54 slots) - bigger isn't always better, plan your layout")
        private int rows = 3;

        @Comment("Material for empty slots - leave blank if you like ugly GUIs")
        private String filler = "GRAY_STAINED_GLASS_PANE";

        @Comment("Sound when clicked interactive item")
        private ClickSound clickSound = new ClickSound(false, "ui.button.click", 50);

        public record ClickSound(boolean enabled, String sound, Integer volume) {}
    }
}
