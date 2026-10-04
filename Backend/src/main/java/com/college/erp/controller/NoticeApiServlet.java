package com.college.erp.controller;

import com.college.erp.entity.Notice;
import com.college.erp.service.NoticeService;
import com.college.erp.service.impl.NoticeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/notices", "/api/notices/*"})
public class NoticeApiServlet extends HttpServlet {

    private final NoticeService noticeService = new NoticeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String roleParam = request.getParameter("targetRole");
            List<Notice> list = (roleParam != null && !roleParam.isBlank())
                    ? noticeService.getNoticesByRole(roleParam.trim())
                    : noticeService.getAllNotices();

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
            Notice notice = new Notice();
            notice.setTitle(data.get("title"));
            notice.setContent(data.get("content"));
            notice.setCreatedBy(Integer.parseInt(data.getOrDefault("createdBy", "1")));
            notice.setTargetRole(data.getOrDefault("targetRole", "ALL"));
            notice.setPublishDate(LocalDateTime.now());
            notice.setActive(true);

            noticeService.saveNotice(notice);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Notice published successfully\",\"notice\":" + toJson(notice) + "}");
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
                response.getWriter().write("{\"error\":\"Notice ID required for delete\"}");
                return;
            }
            int id = Integer.parseInt(path.startsWith("/") ? path.substring(1) : path);
            noticeService.deleteNotice(id);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Notice deleted\"}");
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

    private String toJsonList(List<Notice> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Notice n) {
        return "{"
                + "\"noticeId\":" + n.getNoticeId() + ","
                + "\"title\":\"" + escapeJson(n.getTitle()) + "\","
                + "\"content\":\"" + escapeJson(n.getContent()) + "\","
                + "\"targetRole\":\"" + escapeJson(n.getTargetRole()) + "\","
                + "\"publishDate\":\"" + (n.getPublishDate() != null ? n.getPublishDate().toString() : "") + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
