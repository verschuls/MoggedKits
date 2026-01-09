package me.verschuls.tren.utils;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    @SuppressWarnings("UnstableApiUsage")
    public static ItemStack blankItem(Material material) {
        ItemStack blank = new ItemStack(material);
        /*blank.setData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        blank.setData(DataComponentTypes.HIDE_TOOLTIP);*/
        TooltipDisplay display = TooltipDisplay.tooltipDisplay().hideTooltip(true).addHiddenComponents(DataComponentTypes.ITEM_NAME, DataComponentTypes.TOOLTIP_DISPLAY, DataComponentTypes.RARITY).build();
        blank.setData(DataComponentTypes.TOOLTIP_DISPLAY, display);
        ItemMeta meta = blank.getItemMeta();
        meta.customName(Component.empty());
        blank.setItemMeta(meta);
        return blank;
    }
}
