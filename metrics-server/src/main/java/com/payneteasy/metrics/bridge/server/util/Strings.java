package com.payneteasy.metrics.bridge.server.util;

public class Strings {

    public static boolean isEmpty(String aText) {
        return aText == null || aText.isEmpty() || aText.trim().isEmpty();
    }
}
