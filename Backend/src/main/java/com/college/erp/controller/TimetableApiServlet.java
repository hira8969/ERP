package com.college.erp.controller;

import com.college.erp.entity.Timetable;
import com.college.erp.service.TimetableService;
import com.college.erp.service.impl.TimetableServiceImpl;
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

@WebServlet(urlPatterns = {"/api/timetables", "/api/timetables/*"})
public class TimetableApiServlet extends HttpServlet {

    private final TimetableService timetableService = new TimetableServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String courseParam = request.getParameter("courseId");
            String semParam = request.getParameter("semesterId");
            String facultyParam = request.getParameter("facultyId");

            List<Timetable> list;
            if (facultyParam != null && !facultyParam.isBlank()) {
                list = timetableService.getTimetableByFaculty(Integer.parseInt(facultyParam.trim()));
            } else if (courseParam != null && semParam != null) {
                list = timetableService.getTimetableByCourseAndSemester(
                        Integer.parseInt(courseParam.trim()),
                        Integer.parseInt(semParam.trim())
                );
            } else {
                list = timetableService.getAllTimetables();
            }

            response.getWriter().write(toJsonList(list));
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
            Timetable tt = new Timetable();
            tt.setCourseId(Integer.parseInt(data.get("courseId")));
            tt.setSemesterId(Integer.parseInt(data.get("semesterId")));
            tt.setSubjectId(Integer.parseInt(data.get("subjectId")));
            tt.setFacultyId(Integer.parseInt(data.get("facultyId")));
            tt.setDayOfWeek(data.get("dayOfWeek"));
            tt.setStartTime(data.get("startTime"));
            tt.setEndTime(data.get("endTime"));
            tt.setRoomNumber(data.get("roomNumber"));

            timetableService.saveTimetable(tt);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Timetable slot saved successfully\"}");
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
                response.getWriter().write("{\"error\":\"Timetable ID required for delete\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            timetableService.deleteTimetable(id);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Timetable slot deleted\"}");
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
                        map.put(kv[0].trim().replace("\"", ""), kv[1].trim().replace("\"", ""));
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

    private String toJsonList(List<Timetable> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Timetable t) {
        return "{"
                + "\"timetableId\":" + t.getTimetableId() + ","
                + "\"courseId\":" + t.getCourseId() + ","
                + "\"semesterId\":" + t.getSemesterId() + ","
                + "\"subjectId\":" + t.getSubjectId() + ","
                + "\"facultyId\":" + t.getFacultyId() + ","
                + "\"dayOfWeek\":\"" + escapeJson(t.getDayOfWeek()) + "\","
                + "\"startTime\":\"" + escapeJson(t.getStartTime()) + "\","
                + "\"endTime\":\"" + escapeJson(t.getEndTime()) + "\","
                + "\"roomNumber\":\"" + escapeJson(t.getRoomNumber()) + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
