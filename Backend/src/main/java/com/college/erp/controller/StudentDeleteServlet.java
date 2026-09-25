package com.college.erp.controller;

import com.college.erp.service.StudentService;
import com.college.erp.service.impl.StudentServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/students/delete")
public class StudentDeleteServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int studentId = StudentFormSupport.parseNumber(request.getParameter("id"));
            studentService.deleteStudent(studentId);
            response.sendRedirect(request.getContextPath() + "/students/list");
        } catch (RuntimeException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }
}