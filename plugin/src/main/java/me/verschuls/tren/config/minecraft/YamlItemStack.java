package me.verschuls.tren.config.minecraft;

import de.exlll.configlib.Configuration;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Setter;
import me.verschuls.tren.modules.placeholder.Placeholder;
import me.verschuls.tren.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
public class YamlItemStack {

    @Setter
    private String material = "";
    private String name = "";
    private Integer amount = 1;
    private List<String> lore = new ArrayList<>();
    private String[] enchants = {};
    private String[] flags = {};

    private YamlItemStack() {}
    public static YamlItemStack create() {
        return new YamlItemStack();
    }

    public static YamlItemStack create(String material) {
        YamlItemStack item = new YamlItemStack();
        item.material = material;
        return item;
    }

    public static YamlItemStack create(String name, String material) {
        YamlItemStack item = new YamlItemStack();
        item.name = name;
        item.material = material;
        return item;
    }

    public ItemStack format() {
        return format_(this.material, null);
    }

    public ItemStack format(Player p) {
        return format_(this.material, p);
    }

    private ItemStack format_(String material, @Nullable Player p) {
        if (material == null) return null;
        Material mat = Material.valueOf(material);
        ItemStack stack = new ItemStack(mat);
        stack.setAmount(amount > mat.getMaxStackSize() ? mat.getMaxStackSize() : amount);
        ItemMeta meta = stack.getItemMeta();
        if (!name.isBlank()) meta.displayName(TextUtils.format(p == null ? name : Placeholder.get().parse(p, name)));
        if (!lore.isEmpty()) meta.lore(TextUtils.formatList(p == null ? lore : Placeholder.get().parse(p, lore)));
        if (enchants.length > 0) {
            for (String enchant : enchants) {
                String[] split = enchant.split(":");
                meta.addEnchant(Objects.requireNonNull(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(split[0]))),
                        Integer.parseInt(split[1]), true);
            }
        }
        if  (flags.length > 0)
            Arrays.stream(flags).map(ItemFlag::valueOf).forEach(meta::addItemFlags);
        stack.setItemMeta(meta);
        return stack;
    }

    public YamlItemStack override(YamlItemStack item) {
        if (!item.material.isBlank()) material = item.material;
        if (!item.name.isEmpty()) name = item.name;
        if (item.amount > amount) amount = item.amount;
        if (!item.lore.isEmpty()) lore = item.lore;
        if (item.enchants.length > 0) enchants = item.enchants;
        if (item.flags.length > 0) flags = item.flags;
        return this;
    }

    @Override
    public YamlItemStack clone() {
        YamlItemStack clone = new YamlItemStack();
        clone.material = material;
        clone.name = name;
        clone.amount = amount;
        clone.lore = lore;
        clone.enchants = enchants;
        clone.flags = flags;
        return clone;
    }
}
