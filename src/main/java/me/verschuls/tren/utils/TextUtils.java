package me.verschuls.tren.utils;

import me.verschuls.cbu.CIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TextUtils {

    private static final LegacyComponentSerializer formater = LegacyComponentSerializer.builder().character('&').extractUrls().hexCharacter('#').hexColors().build();

    public static Component format(String text) {
        return formater.deserialize(text);
    }

    public static Component format(List<String> text) {
        AtomicReference<Component> component = new AtomicReference<>(Component.empty());
        for (String t : text) component.set(component.get().append(formater.deserialize(t).appendNewline()));
        return component.get();
    }

    public static List<Component> formatList(List<String> text) {
        return text.stream().map(TextUtils::format).toList();
    }

    public static Component format(String... text) {
        return format(List.of(text));
    }

    public static List<Component> formatList(String... text) {
        return formatList(List.of(text));
    }

    public static Component blank() {
        return formater.deserialize("&7");
    }
}
