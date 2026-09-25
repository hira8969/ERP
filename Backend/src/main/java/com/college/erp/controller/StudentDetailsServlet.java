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

@WebServlet("/students/details")
public class StudentDetailsServlet extends HttpServlet {

    private final StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int studentId = StudentFormSupport.parseNumber(request.getParameter("id"));
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
                return;
            }
            request.setAttribute("student", student);
                request.getRequestDispatcher("/student-details.html")
                    .forward(request, response);
        } catch (RuntimeException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }
}