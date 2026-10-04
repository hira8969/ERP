package com.college.erp.controller;

import com.college.erp.entity.Department;
import com.college.erp.service.DepartmentService;
import com.college.erp.service.impl.DepartmentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/api/departments", "/api/departments/*"})
public class DepartmentApiServlet extends HttpServlet {

    private final DepartmentService departmentService = new DepartmentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/") || path.isBlank()) {
                List<Department> list = departmentService.getAllDepartments();
                response.getWriter().write(toJsonList(list));
                return;
            }

            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            Department dept = departmentService.getDepartmentById(id);
            if (dept == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Department not found\"}");
                return;
            }
            response.getWriter().write(toJson(dept));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid department ID\"}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private String toJsonList(List<Department> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Department d) {
        return "{"
                + "\"departmentId\":" + d.getDepartmentId() + ","
                + "\"departmentCode\":\"" + escapeJson(d.getDepartmentCode()) + "\","
                + "\"departmentName\":\"" + escapeJson(d.getDepartmentName()) + "\","
                + "\"description\":\"" + escapeJson(d.getDescription()) + "\","
                + "\"hodFacultyId\":" + (d.getHodFacultyId() == null ? "null" : d.getHodFacultyId()) + ","
                + "\"active\":" + d.isActive()
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
