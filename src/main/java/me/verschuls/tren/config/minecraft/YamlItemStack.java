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
public abstract class YamlItemStack {

    public static YamlItemStack.Basic basic() {
        return new YamlItemStack.Basic();
    }

    public static YamlItemStack.Basic basic(String name, String material) {
        YamlItemStack.Basic item = new YamlItemStack.Basic();
        item.material = material;
        item.name = name;
        return item;
    }

    public static YamlItemStack.Section section() {
        return new YamlItemStack.Section();
    }

    protected String name = "";
    protected Integer amount = 1;
    protected List<String> lore = new ArrayList<>();
    protected String[] enchants = {};
    protected String[] flags = {};

    public static class Section extends YamlItemStack {
        private Section() {}
        public ItemStack format(String material) {
            return this.format_(material, null);
        }

        public ItemStack format(String material, Player p) {
            return this.format_(material, p);
        }
    }
    public static class Basic extends YamlItemStack {
        private Basic() {}
        @Setter
        private String material = "";

        public ItemStack format() {
            return format_(this.material, null);
        }

        public ItemStack format(Player p) {
            return format_(this.material, p);
        }

        public Basic override(YamlItemStack.Basic item) {
            if (item.material != null) material = item.material;
            if (!item.name.isEmpty()) name = item.name;
            if (item.amount > amount) amount = item.amount;
            if (!item.lore.isEmpty()) lore = item.lore;
            if (item.enchants.length > 0) enchants = item.enchants;
            if (item.flags.length > 0) flags = item.flags;
            return this;
        }

        @Override
        public Basic clone() {
            Basic clone = new Basic();
            clone.material = material;
            clone.name = name;
            clone.amount = amount;
            clone.lore = lore;
            clone.enchants = enchants;
            clone.flags = flags;
            return clone;
        }
    }

    protected ItemStack format_(String material, @Nullable Player p) {
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
}
