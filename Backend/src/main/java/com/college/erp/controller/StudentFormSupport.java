package com.college.erp.controller;

import com.college.erp.entity.Student;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

final class StudentFormSupport {

    private StudentFormSupport() {
    }

    static Student fromRequest(HttpServletRequest request) {
        Student student = new Student();
        student.setAdmissionNumber(clean(request.getParameter("admissionNumber")));
        student.setRollNumber(clean(request.getParameter("rollNumber")));
        student.setFirstName(clean(request.getParameter("firstName")));
        student.setLastName(clean(request.getParameter("lastName")));
        student.setGender(clean(request.getParameter("gender")));
        student.setDateOfBirth(parseDate(request.getParameter("dateOfBirth")));
        student.setEmail(clean(request.getParameter("email")));
        student.setPhone(clean(request.getParameter("phone")));
        student.setAddress(clean(request.getParameter("address")));
        student.setCity(clean(request.getParameter("city")));
        student.setState(clean(request.getParameter("state")));
        student.setPincode(clean(request.getParameter("pincode")));
        student.setDepartmentId(parseNumber(request.getParameter("departmentId")));
        student.setCourseId(parseNumber(request.getParameter("courseId")));
        student.setSemesterId(parseNumber(request.getParameter("semesterId")));
        student.setAdmissionDate(parseDate(request.getParameter("admissionDate")));
        student.setGuardianName(clean(request.getParameter("guardianName")));
        student.setGuardianPhone(clean(request.getParameter("guardianPhone")));
        student.setBloodGroup(clean(request.getParameter("bloodGroup")));
        student.setStatus(clean(request.getParameter("status")));
        return student;
    }

    static Student fromMap(Map<String, String> map) {
        Student student = new Student();
        student.setAdmissionNumber(clean(map.get("admissionNumber")));
        student.setRollNumber(clean(map.get("rollNumber")));
        student.setFirstName(clean(map.get("firstName")));
        student.setLastName(clean(map.get("lastName")));
        student.setGender(clean(map.get("gender")));
        student.setDateOfBirth(parseDate(map.get("dateOfBirth")));
        student.setEmail(clean(map.get("email")));
        student.setPhone(clean(map.get("phone")));
        student.setAddress(clean(map.get("address")));
        student.setCity(clean(map.get("city")));
        student.setState(clean(map.get("state")));
        student.setPincode(clean(map.get("pincode")));
        student.setDepartmentId(parseNumber(map.get("departmentId")));
        student.setCourseId(parseNumber(map.get("courseId")));
        student.setSemesterId(parseNumber(map.get("semesterId")));
        student.setAdmissionDate(parseDate(map.get("admissionDate")));
        student.setGuardianName(clean(map.get("guardianName")));
        student.setGuardianPhone(clean(map.get("guardianPhone")));
        student.setBloodGroup(clean(map.get("bloodGroup")));
        student.setStatus(clean(map.get("status")));
        return student;
    }

    static Map<String, String> extractBodyOrParams(HttpServletRequest request) throws IOException {
        Map<String, String> data = new HashMap<>();
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String json = sb.toString().trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                // Simple parser for flat JSON key-value pairs
                String inner = json.substring(1, json.length() - 1);
                // Split by commas not inside quotes
                boolean inQuote = false;
                StringBuilder token = new StringBuilder();
                for (int i = 0; i < inner.length(); i++) {
                    char c = inner.charAt(i);
                    if (c == '"' && (i == 0 || inner.charAt(i - 1) != '\\')) {
                        inQuote = !inQuote;
                    }
                    if (c == ',' && !inQuote) {
                        parseEntry(token.toString(), data);
                        token.setLength(0);
                    } else {
                        token.append(c);
                    }
                }
                if (token.length() > 0) {
                    parseEntry(token.toString(), data);
                }
            }
        } else {
            request.getParameterMap().forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    data.put(k, v[0]);
                }
            });
        }
        return data;
    }

    private static void parseEntry(String entry, Map<String, String> out) {
        int colon = entry.indexOf(':');
        if (colon != -1) {
            String k = entry.substring(0, colon).trim().replaceAll("^\"|\"$", "");
            String v = entry.substring(colon + 1).trim();
            if ("null".equalsIgnoreCase(v)) {
                out.put(k, null);
            } else {
                v = v.replaceAll("^\"|\"$", "");
                out.put(k, v);
            }
        }
    }

    static int parseNumber(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Numeric fields must contain valid numbers");
        }
    }

    static LocalDate parseDate(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (java.time.format.DateTimeParseException exception) {
            throw new IllegalArgumentException("Enter a valid date (YYYY-MM-DD)");
        }
    }

    static String clean(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value.trim();
    }
}