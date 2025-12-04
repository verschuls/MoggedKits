package me.verschuls.tren.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    public static ItemStack blankItem(Material material) {
        ItemStack blank = new ItemStack(material);
        ItemMeta meta = blank.getItemMeta();
        meta.displayName(TextUtils.blank());
        blank.setItemMeta(meta);
        return blank;
    }
}
