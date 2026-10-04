package com.college.erp.controller;

import com.college.erp.entity.User;
import com.college.erp.service.DashboardService;
import com.college.erp.service.impl.DashboardServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/dashboard/stats")
public class DashboardStatsServlet extends HttpServlet {

    private final DashboardService dashboardService = new DashboardServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String role = (user != null) ? user.getRole() : "ADMIN";
        int userId = (user != null) ? user.getUserId() : 1;

        Map<String, Object> stats;
        if ("FACULTY".equalsIgnoreCase(role)) {
            stats = dashboardService.getFacultyStatistics(userId);
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            stats = dashboardService.getStudentStatistics(userId);
        } else {
            stats = dashboardService.getAdminStatistics();
        }

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"role\":\"").append(role).append("\",");
        sb.append("\"stats\":{");
        int count = 0;
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            if (count > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object val = entry.getValue();
            if (val instanceof Number || val instanceof Boolean) {
                sb.append(val);
            } else {
                sb.append("\"").append(val != null ? val.toString().replace("\"", "\\\"") : "").append("\"");
            }
            count++;
        }
        sb.append("}}");

        response.getWriter().write(sb.toString());
    }
}
