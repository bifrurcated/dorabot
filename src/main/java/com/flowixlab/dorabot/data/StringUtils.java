package com.flowixlab.dorabot.data;

import reactor.util.annotation.Nullable;

public class StringUtils {

    /**
     * @param str                The string to abbreviate, may be {@code null}.
     * @param toLastDeleteMarker Deletes characters before this character (the last such character in the string will be found)
     * @param maxWidth           Maximum length of result string, must be at least 5.
     * @return Abbreviated string, {@code null} if null string input.
     * @throws IllegalArgumentException If the width is too small.
     */
    public static String abbreviate(@Nullable String str, @Nullable String toLastDeleteMarker, int maxWidth) {
        if (str == null || toLastDeleteMarker == null) {
            return null;
        }
        final int length = str.length();
        final String abbrevMarker = "...";
        if (length - abbrevMarker.length() <= 0) {
            throw new IllegalArgumentException("Minimum abbreviation width is %d".formatted(abbrevMarker.length() + 1));
        }
        if (length >= maxWidth) {
            StringBuilder sb = new StringBuilder(str);
            int lastIndexOf = sb.lastIndexOf(toLastDeleteMarker);
            int start = lastIndexOf - (length - (maxWidth - abbrevMarker.length()));
            return sb.delete(start, lastIndexOf).insert(start, abbrevMarker).toString();
        }
        return str;
    }
}
