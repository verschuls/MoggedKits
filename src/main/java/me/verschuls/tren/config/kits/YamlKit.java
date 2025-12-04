package me.verschuls.tren.config.kits;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import lombok.Setter;
import me.verschuls.tren.config.minecraft.YamlItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Getter @Setter
public class YamlKit {

    private Integer cooldown = 5;
    private Integer weight = 0;
    private String guiTitle = "";

    private Display display = new Display();

    @Configuration
    @Getter
    public static class Display {
        private YamlItemStack.Basic access = YamlItemStack.basic();
        private YamlItemStack.Basic denied = YamlItemStack.basic();
        private YamlItemStack.Basic cooldown = YamlItemStack.basic();
    }

    private Armor armor = new Armor();

    @Configuration
    @Getter
    public static class Armor {
        private boolean autoEquip = true;
        private YamlItemStack.Basic helmet = YamlItemStack.basic();
        private YamlItemStack.Basic chestplate = YamlItemStack.basic();
        private YamlItemStack.Basic leggings = YamlItemStack.basic();
        private YamlItemStack.Basic boots = YamlItemStack.basic();
    }

    private Map<String, YamlItemStack.Section> items = new LinkedHashMap<>(Map.of("STONE", YamlItemStack.section()));
}
