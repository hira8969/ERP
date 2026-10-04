package com.college.erp.controller;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import com.college.erp.util.AuthCookieUtil;
import com.college.erp.util.PasswordUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        boolean isJson = isJsonRequest(request);

        User user = email == null ? null : userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null && email != null) {
            user = userDAO.findByUsername(email.trim());
        }
        if (user == null || !user.isActive() || password == null
                || !PasswordUtil.matches(password, user.getPassword())) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid email or password\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html?error=Invalid%20email%20or%20password");
            }
            return;
        }

        // Establish session
        request.getSession(true).setAttribute("user", user);

        // Handle remember-me session cookie
        if (rememberMe != null && ("on".equalsIgnoreCase(rememberMe) || "true".equalsIgnoreCase(rememberMe))) {
            AuthCookieUtil.setRememberMeCookie(response, user);
        }

        if (isJson) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{"
                    + "\"status\":\"success\","
                    + "\"message\":\"Login successful\","
                    + "\"user\":{"
                    + "\"userId\":" + user.getUserId() + ","
                    + "\"username\":\"" + user.getUsername() + "\","
                    + "\"email\":\"" + user.getEmail() + "\","
                    + "\"role\":\"" + user.getRole() + "\""
                    + "}"
                    + "}");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        return (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);
    }
}