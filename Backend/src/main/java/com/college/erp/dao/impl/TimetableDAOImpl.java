package com.college.erp.dao.impl;

import com.college.erp.dao.TimetableDAO;
import com.college.erp.entity.Timetable;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAOImpl implements TimetableDAO {

    private static final String SELECT_COLUMNS = "timetable_id, course_id, semester_id, subject_id, faculty_id, day_of_week, start_time, end_time, room_number";

    @Override
    public void saveTimetable(Timetable t) {
        String sql = "INSERT INTO timetables (course_id, semester_id, subject_id, faculty_id, day_of_week, start_time, end_time, room_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, t.getCourseId());
            stmt.setInt(2, t.getSemesterId());
            stmt.setInt(3, t.getSubjectId());
            stmt.setInt(4, t.getFacultyId());
            stmt.setString(5, t.getDayOfWeek());
            stmt.setString(6, t.getStartTime());
            stmt.setString(7, t.getEndTime());
            stmt.setString(8, t.getRoomNumber());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setTimetableId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving timetable: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Timetable> getAllTimetables() {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM timetables ORDER BY timetable_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapTimetable(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetables: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Timetable> getTimetableByCourseAndSemester(int courseId, int semesterId) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM timetables WHERE course_id = ? AND semester_id = ? ORDER BY timetable_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTimetable(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetables by course & semester: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Timetable> getTimetableByFaculty(int facultyId) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM timetables WHERE faculty_id = ? ORDER BY timetable_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTimetable(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetables by faculty: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void deleteTimetable(int timetableId) {
        String sql = "DELETE FROM timetables WHERE timetable_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timetableId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting timetable: " + e.getMessage(), e);
        }
    }

    private Timetable mapTimetable(ResultSet rs) throws SQLException {
        Timetable t = new Timetable();
        t.setTimetableId(rs.getInt("timetable_id"));
        t.setCourseId(rs.getInt("course_id"));
        t.setSemesterId(rs.getInt("semester_id"));
        t.setSubjectId(rs.getInt("subject_id"));
        t.setFacultyId(rs.getInt("faculty_id"));
        t.setDayOfWeek(rs.getString("day_of_week"));
        t.setStartTime(rs.getString("start_time"));
        t.setEndTime(rs.getString("end_time"));
        t.setRoomNumber(rs.getString("room_number"));
        return t;
    }
}
