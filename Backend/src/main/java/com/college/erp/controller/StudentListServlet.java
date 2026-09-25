package com.college.erp.controller;

import com.college.erp.service.StudentService;
import com.college.erp.service.impl.StudentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/students/list")
public class StudentListServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("students", studentService.getAllStudents());
            request.getRequestDispatcher("/student-list.html")
                    .forward(request, response);
        } catch (RuntimeException exception) {
            request.setAttribute("errorMessage", "Unable to load students: " + exception.getMessage());
            request.getRequestDispatcher("/student-list.html")
                    .forward(request, response);
        }
    }
}