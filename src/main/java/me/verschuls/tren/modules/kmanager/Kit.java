package me.verschuls.tren.modules.kmanager;

import lombok.Getter;
import me.verschuls.cbu.CM;
import me.verschuls.tren.config.Config;
import me.verschuls.tren.config.config.YamlGUI;
import me.verschuls.tren.config.kits.YamlKit;
import me.verschuls.tren.config.minecraft.YamlItemStack;
import me.verschuls.tren.modules.gui.GUI;
import me.verschuls.tren.modules.gui.GUIManager;
import me.verschuls.tren.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class Kit {

    private String name;


    private final List<ItemStack> items = new ArrayList<>();
    private final Armor armor;

    Kit(String id, YamlKit yaml) {
        this.name = id;
        yaml.getItems().forEach((name, kit)->{
            items.add(kit.format(name));
        });
        YamlGUI.Kit kitGUI = CM.get(Config.class).getKit_preview();
        ItemStack filler = ItemUtils.blankItem(Material.valueOf(kitGUI.getFiller().toUpperCase()));
        armor = new Armor(yaml.getArmor(), filler);
        GUI.Builder gui = GUI.newBuilder(yaml.getGuiTitle(), 6, "kit_"+name);
        for (int i = 0; i<=53; i++) {
            gui.setItem(i, filler);
        }
        for (int i = kitGUI.getItems_slot_start(); i<=kitGUI.getItems_slot_end(); i++) {
            if (i >= items.size()) gui.setItem(i, null);
            else gui.setItem(i, items.get(i));
        }
        int[] as = kitGUI.getArmor_slots();
        gui.setItem(as[0], armor.get(Armor.Piece.HELMET));
        gui.setItem(as[1], armor.get(Armor.Piece.CHESTPLATE));
        gui.setItem(as[2], armor.get(Armor.Piece.LEGGINGS));
        gui.setItem(as[3], armor.get(Armor.Piece.BOOTS));
        YamlGUI.Kit.StatusIcon status = kitGUI.getStatusIcon();
        if (status.isEnabled()) {
            YamlKit.Display display = yaml.getDisplay();
            YamlItemStack.Basic access = display.getAccess().clone();
            YamlItemStack.Basic cooldown = display.getCooldown().clone();
            YamlItemStack.Basic denied = display.getDenied().clone();
            if (status.isChangedIcon()) {
                access.setMaterial(status.getAccess());
                cooldown.setMaterial(status.getCooldown());
                denied.setMaterial(status.getDenied());
            }
            gui.render(status.getSlot(), (p, id_) -> {
                if (p.hasPermission("moggedkits.kit." + name)) {
                    if (KitManager.get().hasCooldown(p, name)) return cooldown.format(p);
                    return access.format(p);
                }
                return denied.format(p);
            });
            gui.button(status.getSlot(), (event) -> {
                if (event.getClickType().isLeftClick()) {
                    event.getPlayer().performCommand("kit " + name);
                    event.getPlayer().closeInventory();
                }
                if (event.getClickType().isRightClick()) preview(event.getPlayer());
                return null;
            });
        }
        gui.setItem(kitGUI.getReturn_slot(), kitGUI.getReturn_item().format());
        gui.button(kitGUI.getReturn_slot(), (clickEvent -> {
            KitManager.get().openMainMenu(clickEvent.getPlayer());
            return null;
        }));
        GUIManager.get().register(gui.build());
    }

    public void preview(Player p) {
        GUIManager.get().open(p, "kit_"+name);
    }

    public int give(Player p) {
        PlayerInventory inv = p.getInventory();
        List<ItemStack> items_ = new ArrayList<>(items);
        if (!armor.isAutoEquip()) armor.getAdd(items_);
        boolean hasSpace = isEnoughSpace(inv, items_.size());
        if (!hasSpace && CM.get(Config.class).isDropWhenFull()) {
            if (armor.isAutoEquip()) items_.addAll(armor.set(inv));
            return p.give(items_).leftovers().size();
        }
        if (hasSpace) {
            if (armor.isAutoEquip()) items_.addAll(armor.set(inv));
            p.give(items_);
            return 0;
        }
        return -1;
    }

    private static boolean isEnoughSpace(PlayerInventory inv, int amount) {
        AtomicInteger empty = new AtomicInteger(0);
        for (int i = 0; i <= 34; i++) if (inv.getItem(i) == null) empty.addAndGet(1);
        return empty.get() >= amount;
    }

    private static class Armor {
        private final Optional<ItemStack> helmet, chestplate, leggings, boots;
        private final ItemStack filler;
        @Getter
        private final boolean autoEquip;
        Armor(YamlKit.Armor yaml, ItemStack filler) {
            this.filler = filler;
            this.autoEquip = yaml.isAutoEquip();
            helmet = Optional.ofNullable(yaml.getHelmet().format());
            chestplate = Optional.ofNullable(yaml.getChestplate().format());
            leggings = Optional.ofNullable(yaml.getLeggings().format());
            boots = Optional.ofNullable(yaml.getBoots().format());
        }

        ItemStack get(Piece piece) {
            return switch (piece) {
                case HELMET -> helmet.orElse(filler);
                case CHESTPLATE -> chestplate.orElse(filler);
                case LEGGINGS -> leggings.orElse(filler);
                case BOOTS -> boots.orElse(filler);
            };
        }

        void getAdd(List<ItemStack> items) {
            for (Piece p : Piece.values())
                if (!get(p).getType().equals(filler.getType())) items.add(get(p));
        }

        List<ItemStack> set(PlayerInventory inv) {
            List<ItemStack> left = new ArrayList<>();
            for (Piece p : Piece.values())
                if (!get(p).getType().equals(filler.getType()))
                    if (p.has(inv)) left.add(get(p));
                    else p.set(inv, get(p));
            return left;
        }

        enum Piece {
            HELMET,
            CHESTPLATE,
            LEGGINGS,
            BOOTS;

            void set(PlayerInventory inv, ItemStack itemStack) {
                switch (this) {
                    case HELMET -> inv.setHelmet(itemStack);
                    case CHESTPLATE -> inv.setChestplate(itemStack);
                    case LEGGINGS -> inv.setLeggings(itemStack);
                    case BOOTS -> inv.setBoots(itemStack);
                }
            }

            boolean has(PlayerInventory inv) {
                return !(switch (this) {
                    case HELMET -> inv.getHelmet();
                    case CHESTPLATE -> inv.getChestplate();
                    case LEGGINGS -> inv.getLeggings();
                    case BOOTS -> inv.getBoots();
                }).getType().equals(Material.AIR);
            }
        }
    }
}
