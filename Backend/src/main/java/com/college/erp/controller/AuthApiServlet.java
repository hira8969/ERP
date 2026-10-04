package com.college.erp.controller;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import com.college.erp.util.AuthCookieUtil;
import com.college.erp.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/auth", "/api/auth/*"})
public class AuthApiServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        if (path == null) {
            path = "";
        }

        // Endpoint: /api/auth/me or /api/auth/status
        if (path.isEmpty() || path.equals("/") || path.equalsIgnoreCase("/me") || path.equalsIgnoreCase("/status")) {
            User user = resolveUser(request);
            if (user == null) {
                response.getWriter().write("{\"authenticated\":false,\"user\":null}");
            } else {
                response.getWriter().write("{"
                        + "\"authenticated\":true,"
                        + "\"user\":{"
                        + "\"userId\":" + user.getUserId() + ","
                        + "\"username\":\"" + escapeJson(user.getUsername()) + "\","
                        + "\"email\":\"" + escapeJson(user.getEmail()) + "\","
                        + "\"role\":\"" + escapeJson(user.getRole()) + "\""
                        + "}"
                        + "}");
            }
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("{\"error\":\"Endpoint not found\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        if (path == null) {
            path = "";
        }

        if (path.equalsIgnoreCase("/login")) {
            handleApiLogin(request, response);
            return;
        }

        if (path.equalsIgnoreCase("/logout")) {
            handleApiLogout(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("{\"error\":\"Endpoint not found\"}");
    }

    private void handleApiLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = extractParams(request);
        String email = params.get("email");
        String password = params.get("password");
        String rememberMe = params.get("rememberMe");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Email and password are required\"}");
            return;
        }

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            user = userDAO.findByUsername(email.trim());
        }
        if (user == null || !user.isActive() || !PasswordUtil.matches(password, user.getPassword())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid email or password\"}");
            return;
        }

        request.getSession(true).setAttribute("user", user);

        if ("true".equalsIgnoreCase(rememberMe) || "on".equalsIgnoreCase(rememberMe) || "1".equals(rememberMe)) {
            AuthCookieUtil.setRememberMeCookie(response, user);
        }

        response.getWriter().write("{"
                + "\"status\":\"success\","
                + "\"message\":\"Login successful\","
                + "\"user\":{"
                + "\"userId\":" + user.getUserId() + ","
                + "\"username\":\"" + escapeJson(user.getUsername()) + "\","
                + "\"email\":\"" + escapeJson(user.getEmail()) + "\","
                + "\"role\":\"" + escapeJson(user.getRole()) + "\""
                + "}"
                + "}");
    }

    private void handleApiLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        AuthCookieUtil.clearRememberMeCookie(request, response);
        response.getWriter().write("{\"status\":\"success\",\"message\":\"Successfully logged out\"}");
    }

    private User resolveUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                return user;
            }
        }
        User remembered = AuthCookieUtil.getRememberedUser(request, userDAO);
        if (remembered != null) {
            request.getSession(true).setAttribute("user", remembered);
            return remembered;
        }
        return null;
    }

    private Map<String, String> extractParams(HttpServletRequest request) throws IOException {
        Map<String, String> result = new HashMap<>();
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder jsonBuilder = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
            }
            String json = jsonBuilder.toString().trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                String inner = json.substring(1, json.length() - 1);
                String[] pairs = inner.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length == 2) {
                        String key = kv[0].replaceAll("[\"\\s]", "");
                        String val = kv[1].replaceAll("[\"\\s]", "");
                        result.put(key, val);
                    }
                }
            }
        } else {
            request.getParameterMap().forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    result.put(k, v[0]);
                }
            });
        }
        return result;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
