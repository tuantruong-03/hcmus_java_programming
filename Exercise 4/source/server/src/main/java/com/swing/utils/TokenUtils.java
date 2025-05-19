package com.swing.utils;

import com.swing.models.User;

import java.util.Optional;
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

    public static Optional<Object> getValue(String token) {
        var object = tokens.get(token);
        if (object == null) {
            return Optional.empty();
        }
        return Optional.of(object);
    }

    public static Optional<User> getUser(String token) {
        return getValue(token).filter(User.class::isInstance).map(User.class::cast);
    }
}
