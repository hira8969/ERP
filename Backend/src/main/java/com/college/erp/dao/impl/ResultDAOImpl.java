package com.college.erp.dao.impl;

import com.college.erp.dao.ResultDAO;
import com.college.erp.entity.Result;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResultDAOImpl implements ResultDAO {

    private static final String SELECT_COLUMNS = "result_id, student_id, examination_id, subject_id, marks_obtained, grade, grade_point, result_status";

    @Override
    public void saveResult(Result r) {
        String sql = "INSERT INTO results (student_id, examination_id, subject_id, marks_obtained, grade, grade_point, result_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, r.getStudentId());
            stmt.setInt(2, r.getExaminationId());
            stmt.setInt(3, r.getSubjectId());
            stmt.setBigDecimal(4, r.getMarksObtained());
            stmt.setString(5, r.getGrade());
            stmt.setBigDecimal(6, r.getGradePoint());
            stmt.setString(7, r.getResultStatus());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setResultId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving result: " + e.getMessage(), e);
        }
    }

    @Override
    public Result getResultById(int resultId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM results WHERE result_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResult(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding result by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM results WHERE student_id = ? ORDER BY result_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResult(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching results for student: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Result> getResultsBySubject(int subjectId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM results WHERE subject_id = ? ORDER BY result_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResult(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching results by subject: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Result> getAllResults() {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM results ORDER BY result_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResult(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all results: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateResult(Result r) {
        String sql = "UPDATE results SET student_id=?, examination_id=?, subject_id=?, marks_obtained=?, grade=?, grade_point=?, result_status=? WHERE result_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getStudentId());
            stmt.setInt(2, r.getExaminationId());
            stmt.setInt(3, r.getSubjectId());
            stmt.setBigDecimal(4, r.getMarksObtained());
            stmt.setString(5, r.getGrade());
            stmt.setBigDecimal(6, r.getGradePoint());
            stmt.setString(7, r.getResultStatus());
            stmt.setInt(8, r.getResultId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating result: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteResult(int resultId) {
        String sql = "DELETE FROM results WHERE result_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting result: " + e.getMessage(), e);
        }
    }

    private Result mapResult(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setResultId(rs.getInt("result_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setExaminationId(rs.getInt("examination_id"));
        r.setSubjectId(rs.getInt("subject_id"));
        r.setMarksObtained(rs.getBigDecimal("marks_obtained"));
        r.setGrade(rs.getString("grade"));
        r.setGradePoint(rs.getBigDecimal("grade_point"));
        r.setResultStatus(rs.getString("result_status"));
        return r;
    }
}
