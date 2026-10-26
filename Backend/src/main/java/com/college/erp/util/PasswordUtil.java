package com.college.erp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Password utility for college demonstration.
 * Stores passwords in normal readable plain-text format for easy database inspection and teacher viva.
 * Also supports matching against legacy/SHA-256 hashes for full flexibility.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * Saves password in normal readable plain text as requested for transparent database inspection and viva demo.
     */
    public static String hash(String password) {
        return (password == null) ? "" : password.trim();
    }

    /**
     * Matches raw password against stored password.
     * Supports:
     * 1. Direct readable plain text match (Primary).
     * 2. Fallback check if stored password in database is a SHA-256 hash.
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        String cleanRaw = rawPassword.trim();
        String cleanStored = storedPassword.trim();

        // 1. Direct plain text match (clean, readable)
        if (cleanRaw.equals(cleanStored)) {
            return true;
        }

        // 2. Fallback check for SHA-256 hashed password
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(cleanRaw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString().equalsIgnoreCase(cleanStored);
        } catch (Exception e) {
            return false;
        }
    }
}