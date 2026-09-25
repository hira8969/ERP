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

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = email == null ? null : userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null || !user.isActive() || password == null
                || !PasswordUtil.matches(password, user.getPassword())) {
            response.sendRedirect(request.getContextPath() + "/login.html?error=Invalid%20email%20or%20password");
            return;
        }
        request.getSession(true).setAttribute("user", user);
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}