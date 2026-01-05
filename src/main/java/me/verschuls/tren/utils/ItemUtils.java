package me.verschuls.tren.utils;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class ItemUtils {

    @SuppressWarnings("UnstableApiUsage")
    public static ItemStack blankItem(Material material) {
        ItemStack blank = new ItemStack(material);
        Set<DataComponentType.Valued<?>> values = new HashSet<>();
        for (Field field : DataComponentTypes.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(DataComponentType.Valued.class)) {
                try {
                    values.add((DataComponentType.Valued<?>) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        TooltipDisplay display = TooltipDisplay.tooltipDisplay().hideTooltip(true).addHiddenComponents(values.toArray(new DataComponentType[]{})).build();
        blank.setData(DataComponentTypes.TOOLTIP_DISPLAY, display);
        ItemMeta meta = blank.getItemMeta();
        meta.customName(Component.empty());
        blank.setItemMeta(meta);
        return blank;
    }
}
