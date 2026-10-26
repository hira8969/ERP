package com.college.erp.dao.impl;

import com.college.erp.dao.SubjectDAO;
import com.college.erp.entity.Subject;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAOImpl implements SubjectDAO {

    @Override
    public void saveSubject(Subject subject) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, course_id, semester_id, faculty_id, credits, max_marks, pass_marks, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getSubjectCode());
            stmt.setString(2, subject.getSubjectName());
            stmt.setInt(3, subject.getCourseId());
            stmt.setInt(4, subject.getSemesterId());
            stmt.setInt(5, subject.getFacultyId());
            stmt.setInt(6, subject.getCredits());
            stmt.setInt(7, subject.getMaxMarks());
            stmt.setInt(8, subject.getPassMarks());
            stmt.setBoolean(9, subject.isActive());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    subject.setSubjectId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving subject: " + e.getMessage(), e);
        }
    }

    @Override
    public Subject getSubjectById(int subjectId) {
        String sql = "SELECT subject_id, subject_code, subject_name, course_id, semester_id, faculty_id, credits, max_marks, pass_marks, active FROM subjects WHERE subject_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSubject(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding subject by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_id, subject_code, subject_name, course_id, semester_id, faculty_id, credits, max_marks, pass_marks, active FROM subjects WHERE active = true ORDER BY subject_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapSubject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subjects: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Subject> getSubjectsByCourseAndSemester(int courseId, int semesterId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_id, subject_code, subject_name, course_id, semester_id, faculty_id, credits, max_marks, pass_marks, active FROM subjects WHERE course_id = ? AND semester_id = ? AND active = true";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSubject(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subjects by course and semester: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Subject> getSubjectsByFaculty(int facultyId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_id, subject_code, subject_name, course_id, semester_id, faculty_id, credits, max_marks, pass_marks, active FROM subjects WHERE faculty_id = ? AND active = true";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSubject(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subjects by faculty: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateSubject(Subject subject) {
        String sql = "UPDATE subjects SET subject_code=?, subject_name=?, course_id=?, semester_id=?, faculty_id=?, credits=?, max_marks=?, pass_marks=?, active=? WHERE subject_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject.getSubjectCode());
            stmt.setString(2, subject.getSubjectName());
            stmt.setInt(3, subject.getCourseId());
            stmt.setInt(4, subject.getSemesterId());
            stmt.setInt(5, subject.getFacultyId());
            stmt.setInt(6, subject.getCredits());
            stmt.setInt(7, subject.getMaxMarks());
            stmt.setInt(8, subject.getPassMarks());
            stmt.setBoolean(9, subject.isActive());
            stmt.setInt(10, subject.getSubjectId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating subject: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSubject(int subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subject: " + e.getMessage(), e);
        }
    }

    private Subject mapSubject(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setSubjectId(rs.getInt("subject_id"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setCourseId(rs.getInt("course_id"));
        s.setSemesterId(rs.getInt("semester_id"));
        s.setFacultyId(rs.getInt("faculty_id"));
        s.setCredits(rs.getInt("credits"));
        s.setMaxMarks(rs.getInt("max_marks"));
        s.setPassMarks(rs.getInt("pass_marks"));
        s.setActive(rs.getBoolean("active"));
        return s;
    }
}
