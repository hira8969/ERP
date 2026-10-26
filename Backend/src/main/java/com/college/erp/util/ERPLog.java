package com.college.erp.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Console Logger for live CLI demonstration during college reviews and viva.
 * Prints cleanly formatted, timestamped messages to standard output (Tomcat CLI console).
 */
public final class ERPLog {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ERPLog() {
    }

    public static void info(String module, String message) {
        System.out.println("[" + LocalTime.now().format(TIME_FORMAT) + "] [ERP-" + module.toUpperCase() + "] " + message);
    }

    public static void success(String module, String message) {
        System.out.println("[" + LocalTime.now().format(TIME_FORMAT) + "] [ERP-" + module.toUpperCase() + "] [SUCCESS] " + message);
    }

    public static void warn(String module, String message) {
        System.out.println("[" + LocalTime.now().format(TIME_FORMAT) + "] [ERP-" + module.toUpperCase() + "] [WARNING] " + message);
    }

    public static void error(String module, String message) {
        System.err.println("[" + LocalTime.now().format(TIME_FORMAT) + "] [ERP-" + module.toUpperCase() + "] [ERROR] " + message);
    }
}
