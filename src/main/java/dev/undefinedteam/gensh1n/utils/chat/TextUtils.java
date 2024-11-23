package dev.undefinedteam.gensh1n.utils.chat;

import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static String stripMinecraftColorCodes(String s) {
        return COLOR_PATTERN.matcher(s).replaceAll("");
    }
}
