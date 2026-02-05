package me.verschuls.tren.storage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlToH2Converter {

    public static CompletableFuture<Long> convert(YamlStorage yaml, H2Storage h2) {
        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();

            var players = yaml.getAll();
            if (players.isEmpty()) return -1L;

            for (var entry : players.entrySet()) {
                UUID uuid = entry.getKey();
                var data = entry.getValue();
                Map<String, Long> cooldownMap = data.getCooldown();
                if (cooldownMap != null)
                    for (var cd : cooldownMap.entrySet())
                        if (cd.getValue() != null && cd.getValue() > 0)
                            h2.importCooldown(uuid, cd.getKey(), cd.getValue());
            }
            return System.currentTimeMillis() - start;
        });
    }
}