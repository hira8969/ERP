package com.college.erp.controller;

import com.college.erp.entity.Course;
import com.college.erp.service.CourseService;
import com.college.erp.service.impl.CourseServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/api/courses", "/api/courses/*"})
public class CourseApiServlet extends HttpServlet {

    private final CourseService courseService = new CourseServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getPathInfo();
        String deptIdParam = request.getParameter("departmentId");
        try {
            if (path == null || path.equals("/") || path.isBlank()) {
                List<Course> list;
                if (deptIdParam != null && !deptIdParam.isBlank()) {
                    int deptId = Integer.parseInt(deptIdParam.trim());
                    list = courseService.getCoursesByDepartment(deptId);
                } else {
                    list = courseService.getAllCourses();
                }
                response.getWriter().write(toJsonList(list));
                return;
            }

            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            Course course = courseService.getCourseById(id);
            if (course == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Course not found\"}");
                return;
            }
            response.getWriter().write(toJson(course));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid course or department ID\"}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private String toJsonList(List<Course> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Course c) {
        return "{"
                + "\"courseId\":" + c.getCourseId() + ","
                + "\"courseCode\":\"" + escapeJson(c.getCourseCode()) + "\","
                + "\"courseName\":\"" + escapeJson(c.getCourseName()) + "\","
                + "\"departmentId\":" + c.getDepartmentId() + ","
                + "\"duration\":\"" + escapeJson(c.getDuration()) + "\","
                + "\"totalSemesters\":" + c.getTotalSemesters() + ","
                + "\"description\":\"" + escapeJson(c.getDescription()) + "\","
                + "\"active\":" + c.isActive()
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
