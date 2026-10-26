package com.college.erp.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Lightweight JSON utility for standard Servlet responses.
 * Avoids external JSON dependencies while keeping code clean, safe, and readable.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Escapes special characters for safe inclusion inside JSON strings.
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Quotes a string value for JSON output.
     */
    public static String quote(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + escape(value) + "\"";
    }

    /**
     * Sends a standard JSON error response with an HTTP status code.
     */
    public static void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + escape(message) + "\"}");
    }

    /**
     * Sends a standard JSON success message with an HTTP status code.
     */
    public static void sendSuccess(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":\"success\",\"message\":\"" + escape(message) + "\"}");
    }
}
