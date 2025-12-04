package me.verschuls.tren.modules.gui;

import com.github.retrooper.packetevents.event.PacketListener;
import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.modules.kmanager.KitManager;
import me.verschuls.tren.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManager implements Listener, PacketListener {
    private static final GUIManager instance = new GUIManager();

    private static final Map<String, GUI> guis = new ConcurrentHashMap<>();
    private static final Map<String, List<UUID>> users = new ConcurrentHashMap<>();

    public static GUIManager get() {
        return instance;
    }

    private GUIManager() {}

    public void register(GUI gui) {
        if (guis.containsKey(gui.getID())) {
            if (!users.containsKey(gui.getID())) return;
            users.get(gui.getID()).forEach(u-> {
                if (Bukkit.getPlayer(u) != null)
                    Bukkit.getPlayer(u).closeInventory();
            });
        }
        guis.put(gui.getID(), gui);
    }

    public void open(Player player, String id) {
        if (!guis.containsKey(id)) {
            player.sendMessage(TextUtils.format("&cWasn't able to find GUI. Contact DEVELOPER"));
            return;
        }
        Inventory inv = guis.get(id).clone_inv();
        GUI.Holder info = (GUI.Holder) inv.getHolder();
        info.getRenders().forEach((slot, render)->{
            inv.setItem(slot, render.apply(player, id));
        });
        users.computeIfAbsent(id, k -> new ArrayList<>()).add(player.getUniqueId());
        player.openInventory(inv);
    }

    @EventHandler(ignoreCancelled = true)
    void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;
        if (event.getSlot() == -999) return;
        Inventory inv = event.getInventory();
        if (inv.getHolder() == null) return;
        if (!(inv.getHolder() instanceof GUI.Holder info)) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        if (!info.getFunctions().containsKey(slot)) return;
        ItemStack item = info.getFunctions().get(slot).apply(new ClickEvent(p, info.getId(), inv.getItem(slot), event.getClick()));
        if (item != null)
            Bukkit.getScheduler().scheduleSyncDelayedTask(MoggedKits.getInstance(), ()->inv.setItem(slot, item), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player p)) return;
        Inventory inv = event.getInventory();
        if (inv.getHolder() == null) return;
        if (!(inv.getHolder() instanceof GUI.Holder info)) return;
        if (!users.containsKey(info.getId())) return;
        if (!users.get(info.getId()).contains(p.getUniqueId())) return;
        users.get(info.getId()).remove(p.getUniqueId());
    }


}
