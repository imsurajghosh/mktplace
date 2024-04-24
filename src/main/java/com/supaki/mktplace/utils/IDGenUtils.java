package com.supaki.mktplace.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class IDGenUtils {

    private static final int SHORT_ID_LENGTH = 8;

    public static String itemIdGenerate() {
        return "I" + RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    }

    public static String userIdGenerate() {
        return "U" + RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    }

    public static String inventoryIdGenerate() {
        return "IV" + RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    }
}
