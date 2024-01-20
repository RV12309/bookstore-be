package com.ecomerce.postmaster.common;

import java.util.regex.Pattern;

public class Constant {
    private Constant() {
        throw new IllegalStateException("Utility class");
    }

    public static final String TRACE_ID = "TRACE_ID";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/html; charset=utf-8";
    public static final String EMAIL_CHARSET = "utf-8";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^([\\w+-.%]+@[\\w-.]+\\.[A-Za-z]{2,4},?)+$", Pattern.CASE_INSENSITIVE);

}
