package me.verschuls.tren.config.messages;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
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
    private String cant_access = "%prefix% &cThis kit requires a bigger jawline. &e&lGet mogged at our shop.";

    @Comment({"Economy messages for the pay2mog experience",
            "Requires Vault - skip this section if your server runs on hopes and dreams"})
    private Economy economy = new Economy();

    @Comment("Successfully claimed a kit - you're now 20% more chad")
    private String kit_granted = "%prefix% &aKit &e%kit% &aacquired! +15 testosterone, +20% jawline definition!";

    @Comment("When someone's on cooldown but still trying to spam kits")
    private String on_cooldown = "%prefix% &cSlow down turbo! &eKit &6%kit% &eis on cooldown for &6%time%";

    @Comment("Preview message - window shopping for chads")
    private String preview_only = "%prefix% &7You're previewing kit &e%kit% &7- Look but don't touch!";

    @Comment("No kits available - true beta moment")
    private String no_kits_available = "%prefix% &cYou have no kits. Absolute poverty moment.";

    @Comment("Kit already claimed recently")
    private String already_claimed = "%prefix% &cYou already claimed &e%kit% &crecently. Don't be greedy!";

    @Comment("When inventory is full and items can't be given")
    private String inventory_full = "%prefix% &cYour inventory is too full to handle this much power! &eClean up your mess first!";

    @Comment("When inventory is full but items were dropped on ground")
    private String inventory_full_dropped = "%prefix% &eYour pockets were full so &6%dropped% items &egot yeeted on the ground!";

    @Configuration @Getter
    public static class Economy {
        @Comment({"Clickable purchase prompt - time to extract those V-bucks",
                "Placeholders: %kit%, %price%, %confirm% - configurable below"})
        private String buy = "%prefix% &eKit &6%kit% &ecosts &a$%price%$&e. %confirm%";

        @Comment({"When they already own this kit - redirect them to claim it",
                "Placeholders: %kit%"})
        private String bought = "%prefix% &aYou already own &e%kit%&a! Claim it with &6/kit %kit%&a, king.";

        @Comment({"The clickable call-to-action in buy_access - make it irresistible",
                "This is what separates the whales from the minnows"})
        private String confirm = "&6&nClick to ascend!";

        @Comment({"Purchase successful - they've joined the elite",
                "Placeholders: %kit%"})
        private String success = "%prefix% &aYou just copped &e%kit%&a! Your mog level increased significantly!";

        @Comment({"Transaction failed - their card got ratio'd",
                "Placeholders: %kit%"})
        private String failed = "%prefix% &cPurchase failed for &e%kit%&c. Your bank said no. L + broke + cope harder.";
    }
}
