package me.verschuls.tren.modules.placeholder;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.verschuls.tren.MoggedKits;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class PAPI extends PlaceholderExpansion {

    private final MoggedKits plugin;

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of("kit_<kit>_cooldown", "kit_<kit>_rawcooldown" ,"kit_<kit>_title", "kit_<kit>_access", "kits");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mkits";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(",", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");
        if (args.length == 1) {
            Function<Player, String> simple = Placeholder.get().getSimple().get(params);
            return simple == null ? null : simple.apply(player);
        }
        if (args.length == 3) {
            BiFunction<Player, String[], String> complex = Placeholder.get().getComplex().get(args[0]);
            return complex == null ? null : complex.apply(player, new String[]{args[1], args[2]});
        }
        return null;
    }
}
