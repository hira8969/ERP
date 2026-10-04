package com.college.erp.filter;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import com.college.erp.util.AuthCookieUtil;
import com.college.erp.util.PasswordUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebFilter(urlPatterns = {
        "/dashboard",
        "/dashboard.html",
        "/students/*",
        "/student-list.html",
        "/add-student.html",
        "/edit-student.html",
        "/student-details.html",
        "/faculty-list.html",
        "/add-faculty.html",
        "/attendance.html",
        "/results.html",
        "/timetable.html",
        "/courses.html",
        "/notices.html",
        "/users.html",
        "/api/students",
        "/api/students/*",
        "/api/faculty",
        "/api/faculty/*",
        "/api/departments",
        "/api/departments/*",
        "/api/courses",
        "/api/courses/*",
        "/api/subjects",
        "/api/subjects/*",
        "/api/attendance",
        "/api/attendance/*",
        "/api/results",
        "/api/results/*",
        "/api/timetables",
        "/api/timetables/*",
        "/api/notices",
        "/api/notices/*",
        "/api/dashboard/stats",
        "/api/users",
        "/api/users/*"
})
public class AuthFilter implements Filter {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Resolve user from existing HTTP session
        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // 2. HTTP Basic Authentication Support (Header: Authorization: Basic <base64>)
        String authHeader = httpRequest.getHeader("Authorization");
        if (user == null && authHeader != null && authHeader.toLowerCase().startsWith("basic ")) {
            try {
                String base64Credentials = authHeader.substring(6).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                String[] values = credentials.split(":", 2);
                if (values.length == 2) {
                    String loginId = values[0].trim();
                    String rawPass = values[1].trim();
                    User found = userDAO.findByEmail(loginId);
                    if (found == null) {
                        found = userDAO.findByUsername(loginId);
                    }
                    if (found != null && found.isActive() && PasswordUtil.matches(rawPass, found.getPassword())) {
                        user = found;
                        httpRequest.getSession(true).setAttribute("user", user);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // 3. Persistent session cookie auto-login
        if (user == null) {
            user = AuthCookieUtil.getRememberedUser(httpRequest, userDAO);
            if (user != null) {
                httpRequest.getSession(true).setAttribute("user", user);
            }
        }

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean isApi = path.startsWith("/api/");

        // 4. If unauthenticated
        if (user == null) {
            if (isApi) {
                httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Centurion ERP Portal\"");
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required. Please login or provide Basic Auth.\"}");
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html?error=Please%20login%20first");
            }
            return;
        }

        // 5. Role-based authorization check
        if (!hasAccess(user, path, httpRequest.getMethod())) {
            if (isApi) {
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied for role " + user.getRole() + "\"}");
            } else {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
            }
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean hasAccess(User user, String path, String method) {
        String role = user.getRole();
        // Super Admin and Admin have full unrestricted access to all operations
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return true;
        }

        if ("FACULTY".equalsIgnoreCase(role)) {
            if (path.startsWith("/api/dashboard/stats")
                    || path.startsWith("/api/departments")
                    || path.startsWith("/api/courses")
                    || path.startsWith("/api/subjects")
                    || path.startsWith("/api/attendance")
                    || path.startsWith("/api/results")
                    || path.startsWith("/api/timetables")
                    || path.startsWith("/api/notices")) {
                return true;
            }
            if (path.equals("/api/students") || path.startsWith("/api/students/")) {
                return "GET".equalsIgnoreCase(method);
            }
            if (path.equals("/api/faculty") || path.startsWith("/api/faculty/")) {
                return "GET".equalsIgnoreCase(method);
            }
            return path.contains("dashboard")
                    || path.contains("attendance")
                    || path.contains("results")
                    || path.contains("timetable")
                    || path.contains("courses")
                    || path.contains("notices")
                    || path.contains("student-list")
                    || path.contains("student-details")
                    || path.contains("faculty-list")
                    || path.endsWith("/list")
                    || path.endsWith("/details");
        }

        if ("STUDENT".equalsIgnoreCase(role)) {
            if (path.startsWith("/api/dashboard/stats")
                    || path.startsWith("/api/departments")
                    || path.startsWith("/api/courses")
                    || path.startsWith("/api/subjects")
                    || path.startsWith("/api/attendance")
                    || path.startsWith("/api/results")
                    || path.startsWith("/api/timetables")
                    || path.startsWith("/api/notices")) {
                return "GET".equalsIgnoreCase(method);
            }
            if (path.startsWith("/students/details") || path.contains("student-details")) {
                return true;
            }
            return path.contains("dashboard")
                    || path.contains("attendance")
                    || path.contains("results")
                    || path.contains("timetable")
                    || path.contains("courses")
                    || path.contains("notices");
        }

        return false;
    }
}