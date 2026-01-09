package me.verschuls.mkapi;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Main API provider interface for MoggedKits.
 * Access via {@link MoggedKitsAPI#getAPI()}.
 */
public interface MKAPIProvider {

    /**
     * Gets the remaining cooldown for a player's kit in seconds.
     * @param player the player to check
     * @param kit the kit name
     * @return remaining cooldown in seconds, 0 if no cooldown
     */
    long getCooldown(Player player, String kit);

    /**
     * Sets a cooldown for a player's kit.
     * @param player the player
     * @param kit the kit name
     * @param cooldown cooldown duration in seconds
     */
    void setCooldown(Player player, String kit, int cooldown);

    /**
     * Gets a kit by name.
     * @param name the kit name
     * @return the kit, or empty if not found
     */
    Optional<MKit> getKit(String name);

    /**
     * Gets all registered kits.
     * @return list of all kits
     */
    List<MKit> getKits();
}
