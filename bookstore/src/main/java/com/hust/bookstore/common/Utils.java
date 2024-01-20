package com.hust.bookstore.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
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

    public static String generateIsbn() {
        String isbn;
        //generate isbn format EAN-13
        String isbnFormat = "978" + RandomStringUtils.randomNumeric(9);
        isbn = isbnFormat.substring(0, 12);
        int sum = 0;
        for (int i = 0; i < isbn.length(); i++) {
            int digit = Integer.parseInt(isbn.substring(i, i + 1));
            sum += (i % 2 == 0) ? digit * 1 : digit * 3;
        }
        int checkDigit = 10 - (sum % 10);
        if (checkDigit == 10) {
            checkDigit = 0;
        }
        isbn += checkDigit;
        return isbn;
    }
}
