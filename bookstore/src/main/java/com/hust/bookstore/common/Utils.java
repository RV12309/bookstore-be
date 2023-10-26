package com.hust.bookstore.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {
    Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static final int RANDOM_PASSWORD_LENGTH = 6;

    public static String randomPassword() {
        return RandomStringUtils.randomAlphanumeric(RANDOM_PASSWORD_LENGTH);
    }

    public static String removeNonUnicodeCharacter(String name) {
        String unicode = new String(name.getBytes(StandardCharsets.UTF_8));
        String regex = unicode.replaceAll("\\s+", "");
        String temp = Normalizer.normalize(regex, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase(Locale.ROOT);
    }
}
