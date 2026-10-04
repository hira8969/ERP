package com.college.erp.controller;

import com.college.erp.entity.Attendance;
import com.college.erp.service.AttendanceService;
import com.college.erp.service.impl.AttendanceServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/attendance", "/api/attendance/*"})
public class AttendanceApiServlet extends HttpServlet {

    private final AttendanceService attendanceService = new AttendanceServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String studentIdParam = request.getParameter("studentId");
            String subjectIdParam = request.getParameter("subjectId");
            String dateParam = request.getParameter("date");

            if (studentIdParam != null && !studentIdParam.isBlank()) {
                int studentId = Integer.parseInt(studentIdParam.trim());
                List<Attendance> records = attendanceService.getAttendanceByStudent(studentId);
                AttendanceService.AttendanceSummary summary = attendanceService.getStudentAttendanceSummary(studentId);

                StringBuilder sb = new StringBuilder("{");
                sb.append("\"summary\":{");
                sb.append("\"totalClasses\":").append(summary.getTotalClasses()).append(",");
                sb.append("\"presentClasses\":").append(summary.getPresentClasses()).append(",");
                sb.append("\"absentClasses\":").append(summary.getAbsentClasses()).append(",");
                sb.append("\"percentage\":").append(summary.getPercentage()).append(",");
                sb.append("\"eligibleForExam\":").append(summary.isEligibleForExam());
                sb.append("},");
                sb.append("\"records\":").append(toJsonList(records));
                sb.append("}");

                response.getWriter().write(sb.toString());
                return;
            }

            if (subjectIdParam != null && !subjectIdParam.isBlank()) {
                int subjectId = Integer.parseInt(subjectIdParam.trim());
                LocalDate date = (dateParam != null && !dateParam.isBlank())
                        ? LocalDate.parse(dateParam.trim())
                        : LocalDate.now();
                List<Attendance> records = attendanceService.getAttendanceBySubjectAndDate(subjectId, date);
                response.getWriter().write(toJsonList(records));
                return;
            }

            // Return all or empty list
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
            Attendance attendance = new Attendance();
            attendance.setStudentId(Integer.parseInt(data.get("studentId")));
            attendance.setSubjectId(Integer.parseInt(data.get("subjectId")));
            attendance.setFacultyId(Integer.parseInt(data.getOrDefault("facultyId", "1")));
            attendance.setStatus(data.getOrDefault("status", "PRESENT").toUpperCase());
            attendance.setRemarks(data.get("remarks"));
            if (data.containsKey("attendanceDate") && !data.get("attendanceDate").isBlank()) {
                attendance.setAttendanceDate(LocalDate.parse(data.get("attendanceDate")));
            } else {
                attendance.setAttendanceDate(LocalDate.now());
            }

            attendanceService.markAttendance(attendance);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Attendance marked successfully\"}");
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

    private String toJsonList(List<Attendance> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Attendance a) {
        return "{"
                + "\"attendanceId\":" + a.getAttendanceId() + ","
                + "\"studentId\":" + a.getStudentId() + ","
                + "\"subjectId\":" + a.getSubjectId() + ","
                + "\"facultyId\":" + a.getFacultyId() + ","
                + "\"attendanceDate\":\"" + a.getAttendanceDate() + "\","
                + "\"status\":\"" + escapeJson(a.getStatus()) + "\","
                + "\"remarks\":\"" + escapeJson(a.getRemarks()) + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
