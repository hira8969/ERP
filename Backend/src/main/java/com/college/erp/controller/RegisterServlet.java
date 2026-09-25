package com.college.erp.controller;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import com.college.erp.util.PasswordUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = value(request.getParameter("username"));
        String email = value(request.getParameter("email")).toLowerCase();
        String password = request.getParameter("password");
        if (username.isBlank() || !email.endsWith("@gmail.com") || password == null || password.length() < 8) {
            response.sendRedirect(request.getContextPath() + "/register.html?error=Use%20a%20Gmail%20address%20and%20an%208-character%20password");
            return;
        }
        try {
            if (userDAO.findByEmail(email) != null) {
                response.sendRedirect(request.getContextPath() + "/register.html?error=Email%20already%20registered");
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
            response.sendRedirect(request.getContextPath() + "/login.html?registered=true");
        } catch (RuntimeException exception) {
            response.sendRedirect(request.getContextPath() + "/register.html?error=Unable%20to%20create%20account");
        }
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }
}