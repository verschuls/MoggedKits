package me.verschuls.tren.config.messages;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import lombok.Getter;

@Configuration
@Getter
public class MKits {

    @Comment("When someone tries to claim a kit that's as real as their chances")
    private String doesnt_exist = "%prefix% &cThat kit is a myth, like your PvP skills. Try a real one!";

    @Comment("When checking available kits")
    private String available_kits = "%prefix% &eYour arsenal of mogging: &6%kits%";

    @Comment({"VIP only zone - peasants need not apply",
            "Promotes the server store because bills don't pay themselves"})
    private String cant_access = "%prefix% &cThis kit requires a bigger jawline. &e&lGet mogged at &6&lhttps://mcpl.gg";

    @Comment("Successfully claimed a kit - you're now 20% more chad")
    private String kit_granted = "%prefix% &aKit &e%kit% &aacquired! +15 testosterone, +20% jawline definition!";

    @Comment("When someone's on cooldown but still trying to spam kits")
    private String on_cooldown = "%prefix% &cSlow down turbo! &eKit &6%kit% &eis on cooldown for &6%time%";

    @Comment("When they check their cooldown status") @Ignore
    private String cooldown_expired = "%prefix% &aYour &e%kit% &acooldown expired. Time to mog again!";

    @Comment("Preview message - window shopping for chads")
    private String preview_only = "%prefix% &7You're previewing kit &e%kit% &7- Look but don't touch!";

    @Comment("When someone gets a kit gifted to them") @Ignore
    private String kit_gifted = "%prefix% &a%sender% &ejust blessed you with kit &6%kit%&e. What a chad!";

    @Comment("When you successfully gift a kit to someone") @Ignore
    private String kit_gift_sent = "%prefix% &aYou've mogged &e%player% &awith kit &6%kit%&a!";

    @Comment("No kits available - true beta moment")
    private String no_kits_available = "%prefix% &cYou have no kits. Absolute poverty moment.";

    @Comment("Kit already claimed recently")
    private String already_claimed = "%prefix% &cYou already claimed &e%kit% &crecently. Don't be greedy!";

    @Comment("When inventory is full and items can't be given")
    private String inventory_full = "%prefix% &cYour inventory is too full to handle this much power! &eClean up your mess first!";

    @Comment("When inventory is full but items were dropped on ground")
    private String inventory_full_dropped = "%prefix% &eYour pockets were full so &6%dropped% items &egot yeeted on the ground!";
}
