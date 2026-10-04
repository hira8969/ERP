package com.college.erp.controller;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import com.college.erp.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/users", "/api/users/*"})
public class UserApiServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                List<User> list = userDAO.findAll();
                response.getWriter().write(toJsonList(list));
                return;
            }

            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            User user = userDAO.findById(id);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"User not found\"}");
                return;
            }
            response.getWriter().write(toJson(user));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid user ID\"}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, String> data = extractData(request);
            String username = data.get("username");
            String email = data.get("email");
            String password = data.get("password");
            String role = data.getOrDefault("role", "STUDENT").toUpperCase();

            if (username == null || username.isBlank() || email == null || email.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Username and email are required\"}");
                return;
            }

            if (userDAO.findByEmail(email.trim().toLowerCase()) != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\":\"User with this email already exists\"}");
                return;
            }

            if (userDAO.findByUsername(username.trim()) != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\":\"User with this username already exists\"}");
                return;
            }

            if (password == null || password.isBlank()) {
                password = "FACULTY".equalsIgnoreCase(role) ? "faculty123" : "student123";
            }

            User user = new User();
            user.setUsername(username.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPassword(PasswordUtil.hash(password));
            user.setRole(role);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userDAO.save(user);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"User created successfully\",\"user\":" + toJson(user) + "}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"User ID required for update\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            User user = userDAO.findById(id);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"User not found\"}");
                return;
            }

            Map<String, String> data = extractData(request);
            if (data.containsKey("username") && !data.get("username").isBlank()) {
                user.setUsername(data.get("username").trim());
            }
            if (data.containsKey("email") && !data.get("email").isBlank()) {
                user.setEmail(data.get("email").trim().toLowerCase());
            }
            if (data.containsKey("password") && !data.get("password").isBlank()) {
                user.setPassword(PasswordUtil.hash(data.get("password")));
            }
            if (data.containsKey("role") && !data.get("role").isBlank()) {
                user.setRole(data.get("role").trim().toUpperCase());
            }
            if (data.containsKey("active")) {
                user.setActive(Boolean.parseBoolean(data.get("active")));
            }
            user.setUpdatedAt(LocalDateTime.now());

            userDAO.update(user);

            response.getWriter().write("{\"status\":\"success\",\"message\":\"User updated successfully\",\"user\":" + toJson(user) + "}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"User ID required for delete\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            userDAO.delete(id);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"User deleted successfully\"}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private Map<String, String> extractData(HttpServletRequest request) throws IOException {
        Map<String, String> map = new HashMap<>();
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
            }
            String body = sb.toString().trim();
            if (body.startsWith("{") && body.endsWith("}")) {
                body = body.substring(1, body.length() - 1);
                String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length == 2) {
                        String k = kv[0].trim().replace("\"", "");
                        String v = kv[1].trim().replace("\"", "");
                        map.put(k, v);
                    }
                }
            }
        } else {
            for (String name : request.getParameterMap().keySet()) {
                map.put(name, request.getParameter(name));
            }
        }
        return map;
    }

    private String toJsonList(List<User> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(User u) {
        return "{"
                + "\"userId\":" + u.getUserId() + ","
                + "\"username\":\"" + escapeJson(u.getUsername()) + "\","
                + "\"email\":\"" + escapeJson(u.getEmail()) + "\","
                + "\"role\":\"" + escapeJson(u.getRole()) + "\","
                + "\"password\":\"" + escapeJson(u.getPassword()) + "\","
                + "\"active\":" + u.isActive() + ","
                + "\"createdAt\":\"" + (u.getCreatedAt() != null ? u.getCreatedAt().toString() : "") + "\","
                + "\"updatedAt\":\"" + (u.getUpdatedAt() != null ? u.getUpdatedAt().toString() : "") + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
