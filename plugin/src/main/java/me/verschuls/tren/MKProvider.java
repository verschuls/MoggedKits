package me.verschuls.tren;

import me.verschuls.mkapi.MKAPIProvider;
import me.verschuls.mkapi.MKit;
import me.verschuls.tren.modules.kmanager.Kit;
import me.verschuls.tren.modules.kmanager.KitManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MKProvider implements MKAPIProvider {
    @Override
    public long getCooldown(Player player, String kit) {
        return KitManager.get().getRawCooldown(player, kit);
    }

    @Override
    public void setCooldown(Player player, String kit, int cooldown) {
        MoggedKits.getStorage().putCooldown(player, kit, cooldown);
    }

    @Override
    public Optional<MKit> getKit(String name) {
        return Optional.ofNullable(KitManager.get().getKit(name).get().intoAPI());
    }

    @Override
    public List<MKit> getKits() {
        return new ArrayList<>(KitManager.get().kitListFull().stream().map(Kit::intoAPI).toList());
    }
}
