package com.college.erp.dao;

import com.college.erp.entity.Student;

import java.util.List;

public interface StudentDAO {

    void saveStudent(Student student);

    Student getStudentById(int studentId);

    List<Student> getAllStudents();

    void updateStudent(Student student);

    void deleteStudent(int studentId);
}