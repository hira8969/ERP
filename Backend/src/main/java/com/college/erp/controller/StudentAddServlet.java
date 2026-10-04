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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/students/add")
public class StudentAddServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();
    private final com.college.erp.dao.UserDAO userDAO = new com.college.erp.dao.impl.UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/add-student.html")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Student student = StudentFormSupport.fromRequest(request);
            String rawPass = request.getParameter("password");
            if (rawPass == null || rawPass.isBlank()) {
                rawPass = "student123";
            }

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
                    newUser.setPassword(com.college.erp.util.PasswordUtil.hash(rawPass));
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
            response.sendRedirect(request.getContextPath() + "/student-list.html?added=true");
        } catch (RuntimeException exception) {
                String message = URLEncoder.encode(userMessage(exception), StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/add-student.html?error=" + message);
        }
    }

    private String userMessage(RuntimeException exception) {
        if (exception.getMessage() != null && exception.getMessage().contains("Duplicate")) {
            return "Admission number, roll number, or email already exists.";
        }
        return exception.getMessage() == null ? "Unable to save student." : exception.getMessage();
    }
}