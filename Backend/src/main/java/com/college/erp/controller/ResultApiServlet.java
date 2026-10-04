package com.college.erp.controller;

import com.college.erp.entity.Result;
import com.college.erp.service.ResultService;
import com.college.erp.service.impl.ResultServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/results", "/api/results/*"})
public class ResultApiServlet extends HttpServlet {

    private final ResultService resultService = new ResultServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String studentIdParam = request.getParameter("studentId");
            String subjectIdParam = request.getParameter("subjectId");

            if (studentIdParam != null && !studentIdParam.isBlank()) {
                int studentId = Integer.parseInt(studentIdParam.trim());
                ResultService.StudentGradeReport report = resultService.getStudentGradeReport(studentId);

                StringBuilder sb = new StringBuilder("{");
                sb.append("\"studentId\":").append(report.getStudentId()).append(",");
                sb.append("\"totalMarks\":").append(report.getTotalMarks()).append(",");
                sb.append("\"maxMarks\":").append(report.getMaxMarks()).append(",");
                sb.append("\"percentage\":").append(report.getPercentage()).append(",");
                sb.append("\"sgpa\":").append(report.getSgpa()).append(",");
                sb.append("\"overallStatus\":\"").append(escapeJson(report.getOverallStatus())).append("\",");
                sb.append("\"results\":").append(toJsonList(report.getResults()));
                sb.append("}");

                response.getWriter().write(sb.toString());
                return;
            }

            if (subjectIdParam != null && !subjectIdParam.isBlank()) {
                int subjectId = Integer.parseInt(subjectIdParam.trim());
                List<Result> results = resultService.getResultsBySubject(subjectId);
                response.getWriter().write(toJsonList(results));
                return;
            }

            response.getWriter().write("[]");
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, String> data = extractData(request);
            Result result = new Result();
            result.setStudentId(Integer.parseInt(data.get("studentId")));
            result.setExaminationId(Integer.parseInt(data.getOrDefault("examinationId", "1")));
            result.setSubjectId(Integer.parseInt(data.get("subjectId")));
            result.setMarksObtained(new BigDecimal(data.getOrDefault("marksObtained", "0")));

            resultService.recordResult(result);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Marks recorded successfully\",\"result\":" + toJson(result) + "}");
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
                response.getWriter().write("{\"error\":\"Result ID required for delete\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            resultService.deleteResult(id);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Result record deleted successfully\"}");
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

    private String toJsonList(List<Result> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Result r) {
        return "{"
                + "\"resultId\":" + r.getResultId() + ","
                + "\"studentId\":" + r.getStudentId() + ","
                + "\"examinationId\":" + r.getExaminationId() + ","
                + "\"subjectId\":" + r.getSubjectId() + ","
                + "\"marksObtained\":" + r.getMarksObtained() + ","
                + "\"grade\":\"" + escapeJson(r.getGrade()) + "\","
                + "\"gradePoint\":" + r.getGradePoint() + ","
                + "\"resultStatus\":\"" + escapeJson(r.getResultStatus()) + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
