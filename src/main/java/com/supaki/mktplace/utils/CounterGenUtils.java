package com.supaki.mktplace.utils;

import java.time.LocalDate;

public class CounterGenUtils {

    public static String buyerMonthlyKey(String buyerId) {
        LocalDate currentdate = LocalDate.now();
        return buyerId + ":" + currentdate.getMonth().name();
    }

    public static String buyerItemDailyKey(String buyerId, String itemId) {
        LocalDate currentdate = LocalDate.now();
        return buyerId + ":" + itemId + ":" + currentdate;
    }
}
