package me.verschuls.mkapi;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Called when a player claims a kit.
 * Can be cancelled to prevent the kit from being given.
 */
public class KitClaimEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private AtomicBoolean cancelled = new AtomicBoolean(false);

    @Getter
    private final Player player;
    @Getter
    private final MKit kit;

    /**
     * @param player the player claiming the kit
     * @param kit the kit being claimed
     */
    public KitClaimEvent(Player player, MKit kit) {
        this.player = player;
        this.kit = kit;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled.set(cancel);
    }
}