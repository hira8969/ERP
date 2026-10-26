package com.college.erp.dao.impl;

import com.college.erp.dao.CourseDAO;
import com.college.erp.entity.Course;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public Course findById(int courseId) {
        String sql = "SELECT course_id, course_code, course_name, department_id, duration, total_semesters, description, active FROM courses WHERE course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name, department_id, duration, total_semesters, description, active FROM courses WHERE active = true ORDER BY course_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching courses: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Course> findByDepartmentId(int departmentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name, department_id, duration, total_semesters, description, active FROM courses WHERE department_id = ? AND active = true";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching courses by department: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void save(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, department_id, duration, total_semesters, description, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getDepartmentId());
            stmt.setString(4, course.getDuration());
            stmt.setInt(5, course.getTotalSemesters());
            stmt.setString(6, course.getDescription());
            stmt.setBoolean(7, course.isActive());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setCourseId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving course: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Course course) {
        String sql = "UPDATE courses SET course_code=?, course_name=?, department_id=?, duration=?, total_semesters=?, description=?, active=? WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getDepartmentId());
            stmt.setString(4, course.getDuration());
            stmt.setInt(5, course.getTotalSemesters());
            stmt.setString(6, course.getDescription());
            stmt.setBoolean(7, course.isActive());
            stmt.setInt(8, course.getCourseId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating course: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course: " + e.getMessage(), e);
        }
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDepartmentId(rs.getInt("department_id"));
        course.setDuration(rs.getString("duration"));
        course.setTotalSemesters(rs.getInt("total_semesters"));
        course.setDescription(rs.getString("description"));
        course.setActive(rs.getBoolean("active"));
        return course;
    }
}
