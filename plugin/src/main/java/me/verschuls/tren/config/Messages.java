package me.verschuls.tren.config;

import de.exlll.configlib.Comment;
import lombok.Getter;
import me.verschuls.tren.config.messages.MCommon;
import me.verschuls.tren.config.messages.MKits;
import me.verschuls.ylf.BaseConfig;
import me.verschuls.ylf.BaseData;
import me.verschuls.ylf.CVersion;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Messages extends BaseConfig<Messages.Data> {


    public Messages(Path path, Executor executor) {
        super(path, "messages", Data.class, executor);
    }

    @Getter
    @CVersion("1.1")
    public static class Data extends BaseData {

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
