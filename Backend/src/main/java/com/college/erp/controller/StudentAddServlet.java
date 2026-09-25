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
            studentService.saveStudent(student);
            response.sendRedirect(request.getContextPath() + "/students/list");
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