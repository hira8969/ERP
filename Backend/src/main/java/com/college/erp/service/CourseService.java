package com.college.erp.service;

import com.college.erp.entity.Course;
import java.util.List;

public interface CourseService {

    Course getCourseById(int courseId);

    List<Course> getAllCourses();

    List<Course> getCoursesByDepartment(int departmentId);

    void saveCourse(Course course);

    void updateCourse(Course course);

    void deleteCourse(int courseId);
}
