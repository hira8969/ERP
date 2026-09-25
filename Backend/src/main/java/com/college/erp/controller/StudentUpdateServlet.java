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

@WebServlet("/students/update")
public class StudentUpdateServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Student student = StudentFormSupport.fromRequest(request);
            student.setStudentId(StudentFormSupport.parseNumber(request.getParameter("studentId")));
            studentService.updateStudent(student);
            response.sendRedirect(request.getContextPath() + "/students/list");
        } catch (RuntimeException exception) {
                String message = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/edit-student.html?error=" + message);
        }
    }
}