package com.college.erp.service;

import com.college.erp.entity.Student;

import java.util.List;

public interface StudentService {

    void saveStudent(Student student);

    Student getStudentById(int studentId);

    List<Student> getAllStudents();

    void updateStudent(Student student);

    void deleteStudent(int studentId);
}