package com.swingchat.utils;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenUtils {
    private static final ConcurrentHashMap<String, Object> tokens = new ConcurrentHashMap<>();
    private TokenUtils() {}

    public static String register(Object value) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, value);
        return token;
    }

    public static boolean isValid(String token) {
        return tokens.containsKey(token);
    }


    public static void revoke(String token) {
        tokens.remove(token);
    }

    public static Object getValue(String token) {
        return tokens.get(token);
    }
}
