package me.verschuls.tren.modules.placeholder;

import me.verschuls.tren.utils.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {

    private static final Placeholder INSTANCE = new Placeholder();


    private final Map<UUID, Map<String, String>> temps = new ConcurrentHashMap<>();

    private final Map<String, String> statics = new ConcurrentHashMap<>();
    private final Map<String, Function<Player, String>> simple = new ConcurrentHashMap<>();
    private final Map<String, BiFunction<Player, String[], String>> complex = new ConcurrentHashMap<>();

    private static final Pattern PATTERN = Pattern.compile("%([^%]+)%");

    private Placeholder() {}

    public static Placeholder get() {
        return INSTANCE;
    }

    public void setTemp(String identifier, Player player, String replacement) {
        UUID uuid = player.getUniqueId();
        temps.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(identifier, replacement);
    }

    public void cleanTemp(Player player, String... identifiers) {
        UUID uuid = player.getUniqueId();
        Map<String, String> playerTemps = temps.get(uuid);
        if (playerTemps != null) {
            for (String id : identifiers)
                playerTemps.remove(id);
            if (playerTemps.isEmpty()) temps.remove(uuid);
        }
    }

    public void cleanAllTemps(Player player) {
        temps.remove(player.getUniqueId());
    }

    public void registerStatic(String identifier, String replacement) {
        statics.put(identifier, replacement);
    }

    public void registerSimple(String identifier, Function<Player, String> function) {
        simple.put(identifier, function);
    }

    public void registerComplex(String identifier, BiFunction<Player, String[], String> function) {
        complex.put(identifier, function);
    }

    public List<String> parse(@Nullable Player player, List<String> text) {
        List<String> parsed = new ArrayList<>();
        for (String s : text)
            parsed.add(parse(player, s));
        return parsed;
    }

    public String[] parse(@Nullable Player player, String... text) {
        return parse(player, List.of(text)).toArray(String[]::new);
    }

    public String parse(@Nullable Player player, String text) {
        if (text == null || text.isEmpty() || text.indexOf('%') == -1) return text;
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String content = matcher.group(1);
            AtomicReference<String> replacement = new AtomicReference<>(null);

            if (player != null) {
                Map<String, String> playerTemps = temps.get(player.getUniqueId());
                if (playerTemps != null && playerTemps.containsKey(content)) {
                    replacement.set(playerTemps.remove(content));
                    if (playerTemps.isEmpty()) temps.remove(player.getUniqueId());
                }
            }

            if (replacement.get() == null) {
                if (statics.containsKey(content)) replacement.set(statics.get(content));
                else if (player != null) {
                    if (simple.containsKey(content)) replacement.set(simple.get(content).apply(player));
                    else replacement.set(resolveComplex(player, content));
                }
            }

            if (replacement.get() != null) matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement.get()));
            else matcher.appendReplacement(builder, Matcher.quoteReplacement(matcher.group()));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String resolveComplex(Player player, String content) {
        String[] parts = content.split("_");
        StringBuilder currentKey = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) currentKey.append("_");
            currentKey.append(parts[i]);
            String keyString = currentKey.toString();
            if (complex.containsKey(keyString) && i < parts.length - 1) {
                String[] args = Arrays.copyOfRange(parts, i + 1, parts.length);
                try {
                    return complex.get(keyString).apply(player, args);
                } catch (Exception e) {
                    Logger.error("Error occurred while parsing \""+content+"\"", e);
                    return "ERROR";
                }
            }
        }
        return null;
    }
}
