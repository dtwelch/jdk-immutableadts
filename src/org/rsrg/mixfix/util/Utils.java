package org.rsrg.mixfix.util;

public final class Utils {

    private Utils() {}

    public static String dropSpaces(String input) {
        return input.replaceAll("^\\s+", "");
    }

    public static boolean isEof(String input) {
        return input.isEmpty();
    }

}
