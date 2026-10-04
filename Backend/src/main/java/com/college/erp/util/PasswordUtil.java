package com.college.erp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * Saves password in normal readable format as requested for easy college demo and inspection.
     */
    public static String hash(String password) {
        return (password == null) ? "" : password.trim();
    }

    /**
     * Matches raw password against stored password (supports both readable plain text and legacy SHA-256).
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        String cleanRaw = rawPassword.trim();
        String cleanStored = storedPassword.trim();

        // 1. Direct readable match
        if (cleanRaw.equals(cleanStored)) {
            return true;
        }

        // 2. Fallback check if stored password was hashed with SHA-256
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(cleanRaw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equalsIgnoreCase(cleanStored);
        } catch (Exception ex) {
            return false;
        }
    }
}