package me.verschuls.mkapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a kit with its items, armor, and cooldown.
 */
@AllArgsConstructor
@Getter
public class MKit {

    /** Kit name/identifier */
    private String name;
    /** Cooldown in seconds */
    private Long cooldown;
    /** Armor configuration */
    private Armor armor;
    /** Kit items (excluding armor) */
    private List<ItemStack> items;

    /**
     * Represents armor pieces for a kit.
     */
    @AllArgsConstructor
    @Getter
    public static class Armor {
        private final Optional<ItemStack> helmet, chestplate, leggings, boots;

        /**
         * Gets all armor pieces as a list.
         * @return list of present armor pieces
         */
        public List<ItemStack> asList() {
            ArrayList<ItemStack> list = new ArrayList<>();
            helmet.ifPresent(list::add);
            chestplate.ifPresent(list::add);
            leggings.ifPresent(list::add);
            boots.ifPresent(list::add);
            return list;
        }
    }
}
