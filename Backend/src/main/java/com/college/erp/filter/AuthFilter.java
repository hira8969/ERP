package com.college.erp.filter;

import com.college.erp.entity.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/students/*", "/api/students/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        User user = (User) httpRequest.getSession().getAttribute("user");
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html?error=Please%20login%20first");
            return;
        }
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if ((path.startsWith("/students/") || path.equals("/api/students") || path.startsWith("/api/students/"))
            && !hasStudentAccess(user, path)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have access to this page");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean hasStudentAccess(User user, String path) {
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }
        return "FACULTY".equals(user.getRole())
                && (path.endsWith("/list") || path.endsWith("/details")
                || path.equals("/api/students") || path.startsWith("/api/students/"));
    }
}