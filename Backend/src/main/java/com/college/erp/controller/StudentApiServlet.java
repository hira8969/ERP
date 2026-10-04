package com.college.erp.controller;

import com.college.erp.entity.Student;
import com.college.erp.service.StudentService;
import com.college.erp.service.impl.StudentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/api/students", "/api/students/*"})
public class StudentApiServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();
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
                List<Student> students = studentService.getAllStudents();

                if (search != null && !search.isBlank()) {
                    String query = search.trim().toLowerCase();
                    students = students.stream().filter(s ->
                            (s.getAdmissionNumber() != null && s.getAdmissionNumber().toLowerCase().contains(query))
                            || (s.getFirstName() != null && s.getFirstName().toLowerCase().contains(query))
                            || (s.getLastName() != null && s.getLastName().toLowerCase().contains(query))
                            || (s.getEmail() != null && s.getEmail().toLowerCase().contains(query))
                            || (s.getRollNumber() != null && s.getRollNumber().toLowerCase().contains(query))
                    ).collect(Collectors.toList());
                }

                if (deptParam != null && !deptParam.isBlank()) {
                    try {
                        int deptId = Integer.parseInt(deptParam.trim());
                        students = students.stream()
                                .filter(s -> s.getDepartmentId() == deptId)
                                .collect(Collectors.toList());
                    } catch (NumberFormatException ignored) {
                    }
                }

                writeStudents(response, students);
                return;
            }

            int studentId = parseIdFromPath(path);
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Student not found");
                return;
            }
            response.getWriter().write(studentJson(student));
        } catch (NumberFormatException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid student ID");
        } catch (RuntimeException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, String> data = StudentFormSupport.extractBodyOrParams(request);
            Student student = StudentFormSupport.fromMap(data);

            // Auto-create User login credentials for student
            String studentEmail = student.getEmail();
            if (studentEmail != null && !studentEmail.isBlank()) {
                com.college.erp.entity.User existingUser = userDAO.findByEmail(studentEmail.trim());
                if (existingUser == null) {
                    com.college.erp.entity.User newUser = new com.college.erp.entity.User();
                    String username = (student.getRollNumber() != null && !student.getRollNumber().isBlank())
                            ? student.getRollNumber()
                            : student.getAdmissionNumber();
                    newUser.setUsername(username != null && !username.isBlank() ? username : "student_" + System.currentTimeMillis() % 10000);
                    newUser.setEmail(studentEmail.trim().toLowerCase());
                    String rawPass = data.getOrDefault("password", "student123");
                    newUser.setPassword(com.college.erp.util.PasswordUtil.hash(rawPass.isBlank() ? "student123" : rawPass));
                    newUser.setRole("STUDENT");
                    newUser.setActive(true);
                    newUser.setCreatedAt(java.time.LocalDateTime.now());
                    newUser.setUpdatedAt(java.time.LocalDateTime.now());
                    userDAO.save(newUser);
                    student.setUserId(newUser.getUserId());
                } else {
                    student.setUserId(existingUser.getUserId());
                }
            }

            studentService.saveStudent(student);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{"
                    + "\"status\":\"success\","
                    + "\"message\":\"Student and login credentials created successfully\","
                    + "\"student\":" + studentJson(student)
                    + "}");
        } catch (RuntimeException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Student ID path parameter required for PUT");
                return;
            }
            int studentId = parseIdFromPath(path);
            Student existing = studentService.getStudentById(studentId);
            if (existing == null) {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Student not found");
                return;
            }

            Map<String, String> data = StudentFormSupport.extractBodyOrParams(request);
            Student updated = StudentFormSupport.fromMap(data);
            updated.setStudentId(studentId);
            updated.setCreatedAt(existing.getCreatedAt());

            studentService.updateStudent(updated);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{"
                    + "\"status\":\"success\","
                    + "\"message\":\"Student updated successfully\","
                    + "\"student\":" + studentJson(updated)
                    + "}");
        } catch (NumberFormatException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid student ID");
        } catch (RuntimeException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Student ID path parameter required for DELETE");
                return;
            }
            int studentId = parseIdFromPath(path);
            studentService.deleteStudent(studentId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Student deleted successfully\",\"studentId\":" + studentId + "}");
        } catch (NumberFormatException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid student ID");
        } catch (RuntimeException exception) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }

    private int parseIdFromPath(String path) {
        String trimmed = path.startsWith("/") ? path.substring(1) : path;
        int slashIndex = trimmed.indexOf('/');
        if (slashIndex != -1) {
            trimmed = trimmed.substring(0, slashIndex);
        }
        return Integer.parseInt(trimmed);
    }

    private void writeStudents(HttpServletResponse response, List<Student> students) throws IOException {
        response.getWriter().write("[");
        for (int index = 0; index < students.size(); index++) {
            if (index > 0) {
                response.getWriter().write(",");
            }
            response.getWriter().write(studentJson(students.get(index)));
        }
        response.getWriter().write("]");
    }

    private String studentJson(Student student) {
        return "{" +
                field("studentId", student.getStudentId()) + "," +
                field("admissionNumber", student.getAdmissionNumber()) + "," +
                field("rollNumber", student.getRollNumber()) + "," +
                field("firstName", student.getFirstName()) + "," +
                field("lastName", student.getLastName()) + "," +
                field("gender", student.getGender()) + "," +
                field("dateOfBirth", student.getDateOfBirth()) + "," +
                field("email", student.getEmail()) + "," +
                field("phone", student.getPhone()) + "," +
                field("address", student.getAddress()) + "," +
                field("city", student.getCity()) + "," +
                field("state", student.getState()) + "," +
                field("pincode", student.getPincode()) + "," +
                field("departmentId", student.getDepartmentId()) + "," +
                field("courseId", student.getCourseId()) + "," +
                field("semesterId", student.getSemesterId()) + "," +
                field("admissionDate", student.getAdmissionDate()) + "," +
                field("guardianName", student.getGuardianName()) + "," +
                field("guardianPhone", student.getGuardianPhone()) + "," +
                field("bloodGroup", student.getBloodGroup()) + "," +
                field("status", student.getStatus()) +
                "}";
    }

    private String field(String name, Object value) {
        return quote(name) + ":" + (value == null ? "null" : quote(value.toString()));
    }

    private String quote(String value) {
        return "\"" + value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n") + "\"";
    }

    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\":\"" + quoteSafe(message) + "\"}");
    }

    private String quoteSafe(String value) {
        if (value == null) return "Unknown error";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}