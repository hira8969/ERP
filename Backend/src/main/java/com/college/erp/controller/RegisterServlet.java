package com.college.erp.controller;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import com.college.erp.util.ERPLog;
import com.college.erp.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Handles student self-registration.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = clean(request.getParameter("username"));
        String email = clean(request.getParameter("email")).toLowerCase();
        String password = request.getParameter("password");

        if (username.isBlank() || email.isBlank() || password == null || password.length() < 6) {
            String msg = URLEncoder.encode("Please enter a valid username, email, and password (min 6 characters).", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/register.html?error=" + msg);
            return;
        }

        try {
            if (userDAO.findByEmail(email) != null) {
                String msg = URLEncoder.encode("Email is already registered. Please sign in.", StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/register.html?error=" + msg);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(PasswordUtil.hash(password));
            user.setRole("STUDENT");
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userDAO.save(user);
            ERPLog.success("AUTH", "New student user self-registered: '" + username + "' (" + email + ")");
            response.sendRedirect(request.getContextPath() + "/login.html?registered=true");
        } catch (Exception exception) {
            ERPLog.error("AUTH", "Registration failed: " + exception.getMessage());
            String msg = URLEncoder.encode("Unable to create account: " + exception.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/register.html?error=" + msg);
        }
    }

    private String clean(String input) {
        return input == null ? "" : input.trim();
    }
}
