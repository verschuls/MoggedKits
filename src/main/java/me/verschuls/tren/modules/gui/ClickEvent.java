package me.verschuls.tren.modules.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
@AllArgsConstructor @Getter
public class ClickEvent {
    private final Player player;
    private final String id;
    private final ItemStack clickedItem;
    private final ClickType clickType;
}
