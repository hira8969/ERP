package com.college.erp.controller;

import com.college.erp.entity.Subject;
import com.college.erp.service.SubjectService;
import com.college.erp.service.impl.SubjectServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/subjects", "/api/subjects/*"})
public class SubjectApiServlet extends HttpServlet {

    private final SubjectService subjectService = new SubjectServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                String courseParam = request.getParameter("courseId");
                String semParam = request.getParameter("semesterId");

                List<Subject> list;
                if (courseParam != null && semParam != null) {
                    int courseId = Integer.parseInt(courseParam.trim());
                    int semId = Integer.parseInt(semParam.trim());
                    list = subjectService.getSubjectsByCourseAndSemester(courseId, semId);
                } else {
                    list = subjectService.getAllSubjects();
                }
                response.getWriter().write(toJsonList(list));
                return;
            }

            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            Subject subject = subjectService.getSubjectById(id);
            if (subject == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Subject not found\"}");
                return;
            }
            response.getWriter().write(toJson(subject));
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid subject ID\"}");
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
            Subject subject = new Subject();
            subject.setSubjectCode(data.get("subjectCode"));
            subject.setSubjectName(data.get("subjectName"));
            subject.setCourseId(Integer.parseInt(data.getOrDefault("courseId", "1")));
            subject.setSemesterId(Integer.parseInt(data.getOrDefault("semesterId", "1")));
            subject.setCredits(Integer.parseInt(data.getOrDefault("credits", "4")));
            subject.setMaxMarks(Integer.parseInt(data.getOrDefault("maxMarks", "100")));
            subject.setPassMarks(Integer.parseInt(data.getOrDefault("passMarks", "40")));
            subject.setActive(true);

            subjectService.saveSubject(subject);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Subject created successfully\",\"subject\":" + toJson(subject) + "}");
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

    private String toJsonList(List<Subject> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Subject s) {
        return "{"
                + "\"subjectId\":" + s.getSubjectId() + ","
                + "\"subjectCode\":\"" + escapeJson(s.getSubjectCode()) + "\","
                + "\"subjectName\":\"" + escapeJson(s.getSubjectName()) + "\","
                + "\"courseId\":" + s.getCourseId() + ","
                + "\"semesterId\":" + s.getSemesterId() + ","
                + "\"facultyId\":" + s.getFacultyId() + ","
                + "\"credits\":" + s.getCredits() + ","
                + "\"maxMarks\":" + s.getMaxMarks() + ","
                + "\"passMarks\":" + s.getPassMarks() + ","
                + "\"active\":" + s.isActive()
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
