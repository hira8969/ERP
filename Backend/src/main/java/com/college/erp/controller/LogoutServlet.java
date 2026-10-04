package com.college.erp.controller;

import com.college.erp.util.AuthCookieUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        AuthCookieUtil.clearRememberMeCookie(request, response);

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        if (isJson) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Successfully logged out\"}");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.html?loggedOut=true");
        }
    }
}