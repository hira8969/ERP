package com.college.erp.service.impl;

import com.college.erp.dao.CourseDAO;
import com.college.erp.dao.impl.CourseDAOImpl;
import com.college.erp.entity.Course;
import com.college.erp.service.CourseService;

import java.util.List;

public class CourseServiceImpl implements CourseService {

    private final CourseDAO courseDAO;

    public CourseServiceImpl() {
        this.courseDAO = new CourseDAOImpl();
    }

    public CourseServiceImpl(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    @Override
    public Course getCourseById(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
        return courseDAO.findById(courseId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    @Override
    public List<Course> getCoursesByDepartment(int departmentId) {
        if (departmentId <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        return courseDAO.findByDepartmentId(departmentId);
    }

    @Override
    public void saveCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course data is required");
        }
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) {
            throw new IllegalArgumentException("Course code is required");
        }
        if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }
        if (course.getDepartmentId() <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        course.setActive(true);
        courseDAO.save(course);
    }

    @Override
    public void updateCourse(Course course) {
        if (course == null || course.getCourseId() <= 0) {
            throw new IllegalArgumentException("Valid course ID is required");
        }
        courseDAO.update(course);
    }

    @Override
    public void deleteCourse(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be positive");
        }
        courseDAO.delete(courseId);
    }
}
