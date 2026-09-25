package com.college.erp.service.impl;

import com.college.erp.dao.StudentDAO;
import com.college.erp.dao.impl.StudentDAOImpl;
import com.college.erp.entity.Student;
import com.college.erp.service.StudentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class StudentServiceImpl implements StudentService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final StudentDAO studentDAO;

    public StudentServiceImpl() {
        this.studentDAO = new StudentDAOImpl();
    }

    @Override
    public void saveStudent(Student student) {
        validateStudent(student);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        if (student.getStatus() == null || student.getStatus().isBlank()) {
            student.setStatus("ACTIVE");
        }
        studentDAO.saveStudent(student);
    }

    @Override
    public Student getStudentById(int studentId) {
        validateId(studentId);
        return studentDAO.getStudentById(studentId);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    @Override
    public void updateStudent(Student student) {
        if (student == null || student.getStudentId() <= 0) {
            throw new IllegalArgumentException("A valid student ID is required");
        }
        validateStudent(student);
        student.setUpdatedAt(LocalDateTime.now());
        studentDAO.updateStudent(student);
    }

    @Override
    public void deleteStudent(int studentId) {
        validateId(studentId);
        studentDAO.deleteStudent(studentId);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student data is required");
        }
        if (student.getAdmissionNumber() == null || student.getAdmissionNumber().isBlank()) {
            throw new IllegalArgumentException("Admission number is required");
        }
        if (student.getFirstName() == null || student.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (student.getDepartmentId() <= 0 || student.getCourseId() <= 0 || student.getSemesterId() <= 0) {
            throw new IllegalArgumentException("Department, course and semester IDs must be positive numbers");
        }
        if (student.getEmail() != null && !student.getEmail().isBlank()
                && !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
    }

    private void validateId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }
    }
}