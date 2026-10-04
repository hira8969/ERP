package com.college.erp.controller;

import com.college.erp.entity.Faculty;
import com.college.erp.service.FacultyService;
import com.college.erp.service.impl.FacultyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/api/faculty", "/api/faculty/*"})
public class FacultyApiServlet extends HttpServlet {

    private final FacultyService facultyService = new FacultyServiceImpl();
    private final com.college.erp.dao.UserDAO userDAO = new com.college.erp.dao.impl.UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                String search = request.getParameter("search");
                String deptParam = request.getParameter("departmentId");
                List<Faculty> list = facultyService.getAllFaculty();

                if (search != null && !search.isBlank()) {
                    String query = search.trim().toLowerCase();
                    list = list.stream().filter(f ->
                            (f.getEmployeeNumber() != null && f.getEmployeeNumber().toLowerCase().contains(query))
                            || (f.getFirstName() != null && f.getFirstName().toLowerCase().contains(query))
                            || (f.getLastName() != null && f.getLastName().toLowerCase().contains(query))
                            || (f.getEmail() != null && f.getEmail().toLowerCase().contains(query))
                            || (f.getDesignation() != null && f.getDesignation().toLowerCase().contains(query))
                    ).collect(Collectors.toList());
                }

                if (deptParam != null && !deptParam.isBlank()) {
                    try {
                        int deptId = Integer.parseInt(deptParam.trim());
                        list = list.stream().filter(f -> f.getDepartmentId() == deptId).collect(Collectors.toList());
                    } catch (NumberFormatException ignored) {}
                }

                response.getWriter().write(toJsonList(list));
                return;
            }

            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            Faculty faculty = facultyService.getFacultyById(id);
            if (faculty == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Faculty not found\"}");
                return;
            }
            response.getWriter().write(toJson(faculty));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid faculty ID\"}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, String> data = extractData(request);
            Faculty faculty = new Faculty();
            faculty.setEmployeeNumber(data.getOrDefault("employeeNumber", "FAC" + System.currentTimeMillis() % 10000));
            faculty.setFirstName(data.get("firstName"));
            faculty.setLastName(data.get("lastName"));
            faculty.setGender(data.get("gender"));
            faculty.setEmail(data.get("email"));
            faculty.setPhone(data.get("phone"));
            faculty.setDepartmentId(parseNumber(data.get("departmentId"), 1));
            faculty.setDesignation(data.get("designation"));
            faculty.setQualification(data.get("qualification"));
            faculty.setSpecialization(data.get("specialization"));
            faculty.setCity(data.get("city"));
            faculty.setState(data.get("state"));
            faculty.setStatus("ACTIVE");

            // Auto-create User login credentials for faculty
            String facEmail = faculty.getEmail();
            if (facEmail != null && !facEmail.isBlank()) {
                com.college.erp.entity.User existingUser = userDAO.findByEmail(facEmail.trim());
                if (existingUser == null) {
                    com.college.erp.entity.User newUser = new com.college.erp.entity.User();
                    newUser.setUsername(faculty.getEmployeeNumber());
                    newUser.setEmail(facEmail.trim().toLowerCase());
                    String rawPass = data.getOrDefault("password", "faculty123");
                    newUser.setPassword(com.college.erp.util.PasswordUtil.hash(rawPass.isBlank() ? "faculty123" : rawPass));
                    newUser.setRole("FACULTY");
                    newUser.setActive(true);
                    newUser.setCreatedAt(java.time.LocalDateTime.now());
                    newUser.setUpdatedAt(java.time.LocalDateTime.now());
                    userDAO.save(newUser);
                    faculty.setUserId(newUser.getUserId());
                } else {
                    faculty.setUserId(existingUser.getUserId());
                }
            }

            facultyService.saveFaculty(faculty);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Faculty and login credentials created successfully\",\"faculty\":" + toJson(faculty) + "}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Faculty ID required for update\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            Faculty faculty = facultyService.getFacultyById(id);
            if (faculty == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Faculty not found\"}");
                return;
            }

            Map<String, String> data = extractData(request);
            if (data.containsKey("firstName")) faculty.setFirstName(data.get("firstName"));
            if (data.containsKey("lastName")) faculty.setLastName(data.get("lastName"));
            if (data.containsKey("email")) faculty.setEmail(data.get("email"));
            if (data.containsKey("phone")) faculty.setPhone(data.get("phone"));
            if (data.containsKey("designation")) faculty.setDesignation(data.get("designation"));
            if (data.containsKey("qualification")) faculty.setQualification(data.get("qualification"));
            if (data.containsKey("specialization")) faculty.setSpecialization(data.get("specialization"));
            if (data.containsKey("departmentId")) faculty.setDepartmentId(parseNumber(data.get("departmentId"), faculty.getDepartmentId()));

            facultyService.updateFaculty(faculty);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Faculty updated successfully\",\"faculty\":" + toJson(faculty) + "}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Faculty ID required for delete\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            facultyService.deleteFaculty(id);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Faculty deleted successfully\"}");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private Map<String, String> extractData(HttpServletRequest request) throws IOException {
        Map<String, String> map = new HashMap<>();
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
            }
            String body = sb.toString().trim();
            if (body.startsWith("{") && body.endsWith("}")) {
                body = body.substring(1, body.length() - 1);
                String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length == 2) {
                        String k = kv[0].trim().replace("\"", "");
                        String v = kv[1].trim().replace("\"", "");
                        map.put(k, v);
                    }
                }
            }
        } else {
            for (String name : request.getParameterMap().keySet()) {
                map.put(name, request.getParameter(name));
            }
        }
        return map;
    }

    private int parseNumber(String val, int def) {
        if (val == null || val.isBlank()) return def;
        try { return Integer.parseInt(val.trim()); } catch (Exception e) { return def; }
    }

    private String toJsonList(List<Faculty> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Faculty f) {
        return "{"
                + "\"facultyId\":" + f.getFacultyId() + ","
                + "\"employeeNumber\":\"" + escapeJson(f.getEmployeeNumber()) + "\","
                + "\"firstName\":\"" + escapeJson(f.getFirstName()) + "\","
                + "\"lastName\":\"" + escapeJson(f.getLastName()) + "\","
                + "\"gender\":\"" + escapeJson(f.getGender()) + "\","
                + "\"email\":\"" + escapeJson(f.getEmail()) + "\","
                + "\"phone\":\"" + escapeJson(f.getPhone()) + "\","
                + "\"departmentId\":" + f.getDepartmentId() + ","
                + "\"designation\":\"" + escapeJson(f.getDesignation()) + "\","
                + "\"qualification\":\"" + escapeJson(f.getQualification()) + "\","
                + "\"specialization\":\"" + escapeJson(f.getSpecialization()) + "\","
                + "\"city\":\"" + escapeJson(f.getCity()) + "\","
                + "\"state\":\"" + escapeJson(f.getState()) + "\","
                + "\"status\":\"" + escapeJson(f.getStatus()) + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
