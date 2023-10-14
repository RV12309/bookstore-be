package com.hust.bookstore.common;

import org.apache.commons.lang3.RandomStringUtils;

public class Utils {
    Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static final int RANDOM_PASSWORD_LENGTH = 6;

    public static String randomPassword() {
        return RandomStringUtils.randomAlphanumeric(RANDOM_PASSWORD_LENGTH);
    }
}
