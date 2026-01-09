package me.verschuls.tren.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utils {


    public static String args(String template, String... args) {
        if (args.length == 0) return template;
        StringBuilder result = new StringBuilder(template.length() + args.length * 10);
        int argIndex = 0;
        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);
            if (c == '{' && i + 1 < template.length() && template.charAt(i + 1) == '}') {
                if (argIndex < args.length) result.append(args[argIndex++]);
                else result.append("{}");
                i++; // skip the }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String clean(Collection<String> list) {
        return list.toString().replace("[", "").replace("]", "");
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    public static String cleanName(File file) {
        return file.getName().replaceFirst("\\.(yml|yaml)$", "").toLowerCase();
    }

    @SafeVarargs
    public static<T> List<T> multiList(Collection<T> collection, T... objs) {
        List<T> list_clone = new ArrayList<>(collection);
        list_clone.addAll(List.of(objs));
        return list_clone;
    }
}
