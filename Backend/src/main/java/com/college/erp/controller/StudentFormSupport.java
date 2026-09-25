package com.college.erp.controller;

import com.college.erp.entity.Student;

import java.time.LocalDate;

final class StudentFormSupport {

    private StudentFormSupport() {
    }

    static Student fromRequest(jakarta.servlet.http.HttpServletRequest request) {
        Student student = new Student();
        student.setAdmissionNumber(request.getParameter("admissionNumber"));
        student.setRollNumber(request.getParameter("rollNumber"));
        student.setFirstName(request.getParameter("firstName"));
        student.setLastName(request.getParameter("lastName"));
        student.setGender(request.getParameter("gender"));
        student.setDateOfBirth(parseDate(request.getParameter("dateOfBirth")));
        student.setEmail(request.getParameter("email"));
        student.setPhone(request.getParameter("phone"));
        student.setAddress(request.getParameter("address"));
        student.setCity(request.getParameter("city"));
        student.setState(request.getParameter("state"));
        student.setPincode(request.getParameter("pincode"));
        student.setDepartmentId(parseNumber(request.getParameter("departmentId")));
        student.setCourseId(parseNumber(request.getParameter("courseId")));
        student.setSemesterId(parseNumber(request.getParameter("semesterId")));
        student.setAdmissionDate(parseDate(request.getParameter("admissionDate")));
        student.setGuardianName(request.getParameter("guardianName"));
        student.setGuardianPhone(request.getParameter("guardianPhone"));
        student.setBloodGroup(request.getParameter("bloodGroup"));
        student.setStatus(request.getParameter("status"));
        return student;
    }

    static int parseNumber(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Numeric fields must contain valid numbers");
        }
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException exception) {
            throw new IllegalArgumentException("Enter a valid date");
        }
    }
}