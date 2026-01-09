package me.verschuls.mkapi;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when all kits have been loaded or reloaded.
 */
public class KitsLoadedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final List<MKit> kits;

    /**
     * @param kits list of all loaded kits
     */
    public KitsLoadedEvent(List<MKit> kits) {
        this.kits = kits;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
