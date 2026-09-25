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

@WebServlet("/api/students/*")
public class StudentApiServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/") || path.isBlank()) {
                writeStudents(response, studentService.getAllStudents());
                return;
            }

            Student student = studentService.getStudentById(Integer.parseInt(path.substring(1)));
            if (student == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
                return;
            }
            response.getWriter().write(studentJson(student));
        } catch (RuntimeException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
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
}