package me.verschuls.tren.modules.gui;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import de.exlll.configlib.Comment;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.verschuls.tren.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
public class GUI implements Listener {

    private final Component title;
    private final String ID;
    private final Inventory inventory;

    private GUI(Builder builder) {
        this.title = TextUtils.format(builder.title);
        this.ID = builder.holder.id;
        this.inventory = Bukkit.createInventory(builder.holder, builder.size, TextUtils.format(builder.title));
        builder.items.forEach(inventory::setItem);
    }

    Inventory clone_inv() {
        Inventory inv = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        return inv;
    }

    public static GUI.Builder newBuilder(String title, int rows, String id) {
        return new Builder(title, rows, id);
    }



    public static class Builder {
        private final String title;
        private final int size;
        private final Holder holder;
        private final HashMap<Integer, ItemStack> items = new HashMap<>();
        private Builder(String title, int rows, String id) {
            this.title = title;
            this.size = rows*9;
            this.holder = new Holder(id);
        }

        public GUI.Builder setItem(int slot, ItemStack stack) {
            items.put(slot, stack);
            return this;
        }

        public GUI.Builder button(int slot, Function<ClickEvent, ItemStack> function) {
            this.holder.functions.put(slot, function);
            return this;
        }

        public GUI.Builder render(int slot, BiFunction<Player, String, ItemStack> function) {
            this.holder.renders.put(slot, function);
            return this;
        }

        public GUI build() {
            return new GUI(this);
        }
    }

    @AllArgsConstructor
    @Getter(AccessLevel.PACKAGE)
    static class Holder implements InventoryHolder {
        private final String id;
        private final HashMap<Integer, Function<ClickEvent, ItemStack>> functions = new HashMap<>();
        private final HashMap<Integer, BiFunction<Player, String, ItemStack>> renders = new HashMap<>();

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }

}
