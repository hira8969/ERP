package com.college.erp.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndMatch() {
        String raw = "Admin123@#";
        String saved = PasswordUtil.hash(raw);

        assertNotNull(saved);
        assertEquals(raw, saved); // Normal readable password saved
        assertTrue(PasswordUtil.matches(raw, saved));
        assertFalse(PasswordUtil.matches("WrongPassword", saved));
    }

    @Test
    public void testLegacySha256Match() {
        // Legacy SHA-256 hash of "Admin@123"
        String legacyHash = "e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7";
        assertTrue(PasswordUtil.matches("Admin@123", legacyHash));
        assertFalse(PasswordUtil.matches("Admin@124", legacyHash));
    }
}
