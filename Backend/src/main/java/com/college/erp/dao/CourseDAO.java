package com.college.erp.dao;

import com.college.erp.entity.Course;
import java.util.List;

public interface CourseDAO {

    Course findById(int courseId);

    List<Course> findAll();

    List<Course> findByDepartmentId(int departmentId);

    void save(Course course);

    void update(Course course);

    void delete(int courseId);
}
