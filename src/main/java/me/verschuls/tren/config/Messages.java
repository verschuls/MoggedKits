package me.verschuls.tren.config;

import de.exlll.configlib.Comment;
import lombok.Getter;
import me.verschuls.cbu.BaseConfig;
import me.verschuls.tren.config.messages.MCommon;
import me.verschuls.tren.config.messages.MKits;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Messages extends BaseConfig<Messages.Data> {


    public Messages(Path path, Executor executor) {
        super(path, "messages", Data.class, executor);
    }

    @Getter
    public static class Data extends BaseConfig.Data {
        @Comment("Don't touch this unless you're on TRT and understand config versioning")
        private Double version = 1.0;

        @Comment({"The prefix that mogs all other prefixes. This shows before every message.",
                  "Default: &6[&4Mogged&cKits&r&6] - because your kits need to assert dominance"})
        private String prefix = "&6[&4Mogged&cKits&r&6]";

        @Comment({"Common messages used across whole plugin - the bread and butter of communication",
                "These messages handle all the basic chad-to-beta communication patterns"})
        private MCommon common = new MCommon();

        @Comment({"All kit related messages - where the real mogging happens",
                 "Control how players get flexed on when they try to access kits"})
        private MKits kits = new MKits();
    }
}
