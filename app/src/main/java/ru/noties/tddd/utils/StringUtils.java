package ru.noties.tddd.utils;

public class StringUtils {

    public static int length(String in) {
        return in == null ? 0 : in.length();
    }

    private StringUtils() {}
}
