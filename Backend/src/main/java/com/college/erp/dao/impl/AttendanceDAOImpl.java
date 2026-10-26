package com.college.erp.dao.impl;

import com.college.erp.dao.AttendanceDAO;
import com.college.erp.entity.Attendance;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {

    private static final String SELECT_COLUMNS = "attendance_id, student_id, subject_id, faculty_id, attendance_date, status, remarks";

    @Override
    public void saveAttendance(Attendance a) {
        String sql = "INSERT INTO attendance (student_id, subject_id, faculty_id, attendance_date, status, remarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, a.getStudentId());
            stmt.setInt(2, a.getSubjectId());
            stmt.setInt(3, a.getFacultyId());
            stmt.setDate(4, a.getAttendanceDate() != null ? Date.valueOf(a.getAttendanceDate()) : Date.valueOf(LocalDate.now()));
            stmt.setString(5, a.getStatus() != null ? a.getStatus() : "PRESENT");
            stmt.setString(6, a.getRemarks());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    a.setAttendanceId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving attendance: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveBatchAttendance(List<Attendance> list) {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO attendance (student_id, subject_id, faculty_id, attendance_date, status, remarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Attendance a : list) {
                stmt.setInt(1, a.getStudentId());
                stmt.setInt(2, a.getSubjectId());
                stmt.setInt(3, a.getFacultyId());
                stmt.setDate(4, a.getAttendanceDate() != null ? Date.valueOf(a.getAttendanceDate()) : Date.valueOf(LocalDate.now()));
                stmt.setString(5, a.getStatus() != null ? a.getStatus() : "PRESENT");
                stmt.setString(6, a.getRemarks());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing batch attendance: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM attendance WHERE student_id = ? ORDER BY attendance_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAttendance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching attendance for student: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM attendance WHERE subject_id = ? AND attendance_date = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAttendance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching attendance by subject and date: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM attendance WHERE student_id = ? AND subject_id = ? ORDER BY attendance_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAttendance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching attendance by student and subject: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM attendance ORDER BY attendance_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapAttendance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all attendance: " + e.getMessage(), e);
        }
        return list;
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setSubjectId(rs.getInt("subject_id"));
        a.setFacultyId(rs.getInt("faculty_id"));
        Date d = rs.getDate("attendance_date");
        if (d != null) a.setAttendanceDate(d.toLocalDate());
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        return a;
    }
}
