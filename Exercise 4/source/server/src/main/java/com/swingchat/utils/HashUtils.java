package com.swingchat.utils;

import com.swingchat.types.Result;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    private HashUtils() {}

    public static Result<String> sha256HashToHexString(String input) {
        var hash = sha256Hash(input);
        if (hash.isFailure()) {
            return Result.failure(hash.getException());
        }
        return Result.success(bytesToHex(hash.getValue()));
    }

    public static Result<byte[]> sha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Result.success(hash);
        } catch (NoSuchAlgorithmException e) {
            return Result.failure(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
