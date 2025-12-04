package me.verschuls.tren.config.messages;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MCommon {

    @Comment("When someone tries to do chad stuff without chad permissions")
    private String no_permission = "%prefix% &cNice try beta, but you're missing the permission to do that!";

    @Comment("Console trying to act like a player - bruh moment. Btw no formating here")
    private String only_player = "Console can't use this. Get in the game, literally!";

    @Comment("When that player went to touch grass (or got banned)")
    private String player_offline = "%prefix% &cThat player is offline. Probably mogged too hard and rage quit.";

    @Comment("404: Player not found - skill issue detected")
    private String player_not_found = "%prefix% &cThis player doesn't exist. Check your spelling, king.";

    @Comment("General denial message - for when someone needs to know their place")
    private String denied = "%prefix% &cAbsolutely not happening, chief!";

    @Comment("Cooldown message - patience is a virtue, even for chads")
    private String delay = "%prefix% &eHold your horses! Wait &6%cooldown%s &ebefore trying again.";

    @Comment("When someone invents commands that don't exist")
    private String invalid_cmd = "%prefix% &eThat command is as real as your girlfriend. Try &6/%base%";

    @Comment("Usage hint for those who can't read documentation")
    private String usage = "%prefix% &eLearn to type: &6%usage%";

    @Comment("When plugin is reloaded")
    private String reload = "%prefix% &eReloaded &ein &a%time%ms";
}