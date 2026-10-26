package com.college.erp.dao.impl;

import com.college.erp.dao.FacultyDAO;
import com.college.erp.entity.Faculty;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAOImpl implements FacultyDAO {

    private static final String SELECT_COLUMNS = "faculty_id, user_id, employee_number, first_name, last_name, gender, date_of_birth, email, phone, department_id, designation, qualification, specialization, joining_date, address, city, state, pincode, profile_image, status, created_at, updated_at";

    @Override
    public void saveFaculty(Faculty f) {
        String sql = "INSERT INTO faculty (user_id, employee_number, first_name, last_name, gender, date_of_birth, email, phone, department_id, designation, qualification, specialization, joining_date, address, city, state, pincode, profile_image, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setFacultyParams(stmt, f);
            stmt.setTimestamp(20, f.getCreatedAt() != null ? Timestamp.valueOf(f.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(21, f.getUpdatedAt() != null ? Timestamp.valueOf(f.getUpdatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    f.setFacultyId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving faculty: " + e.getMessage(), e);
        }
    }

    @Override
    public Faculty getFacultyById(int facultyId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapFaculty(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding faculty by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Faculty getFacultyByUserId(int userId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM faculty WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapFaculty(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding faculty by user ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Faculty getFacultyByEmail(String email) {
        if (email == null) return null;
        String sql = "SELECT " + SELECT_COLUMNS + " FROM faculty WHERE LOWER(email) = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapFaculty(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding faculty by email: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM faculty ORDER BY faculty_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapFaculty(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all faculty: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Faculty> getFacultyByDepartment(int departmentId) {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM faculty WHERE department_id = ? ORDER BY faculty_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFaculty(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching faculty by department: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateFaculty(Faculty f) {
        String sql = "UPDATE faculty SET user_id=?, employee_number=?, first_name=?, last_name=?, gender=?, date_of_birth=?, email=?, phone=?, department_id=?, designation=?, qualification=?, specialization=?, joining_date=?, address=?, city=?, state=?, pincode=?, profile_image=?, status=?, updated_at=? WHERE faculty_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setFacultyParams(stmt, f);
            stmt.setTimestamp(20, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(21, f.getFacultyId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating faculty: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFaculty(int facultyId) {
        String sql = "DELETE FROM faculty WHERE faculty_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting faculty: " + e.getMessage(), e);
        }
    }

    private void setFacultyParams(PreparedStatement stmt, Faculty f) throws SQLException {
        stmt.setInt(1, f.getUserId());
        stmt.setString(2, f.getEmployeeNumber());
        stmt.setString(3, f.getFirstName());
        stmt.setString(4, f.getLastName());
        stmt.setString(5, f.getGender());
        stmt.setDate(6, f.getDateOfBirth() != null ? Date.valueOf(f.getDateOfBirth()) : null);
        stmt.setString(7, f.getEmail());
        stmt.setString(8, f.getPhone());
        stmt.setInt(9, f.getDepartmentId());
        stmt.setString(10, f.getDesignation());
        stmt.setString(11, f.getQualification());
        stmt.setString(12, f.getSpecialization());
        stmt.setDate(13, f.getJoiningDate() != null ? Date.valueOf(f.getJoiningDate()) : null);
        stmt.setString(14, f.getAddress());
        stmt.setString(15, f.getCity());
        stmt.setString(16, f.getState());
        stmt.setString(17, f.getPincode());
        stmt.setString(18, f.getProfileImage());
        stmt.setString(19, f.getStatus() != null ? f.getStatus() : "ACTIVE");
    }

    private Faculty mapFaculty(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setFacultyId(rs.getInt("faculty_id"));
        f.setUserId(rs.getInt("user_id"));
        f.setEmployeeNumber(rs.getString("employee_number"));
        f.setFirstName(rs.getString("first_name"));
        f.setLastName(rs.getString("last_name"));
        f.setGender(rs.getString("gender"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) f.setDateOfBirth(dob.toLocalDate());
        f.setEmail(rs.getString("email"));
        f.setPhone(rs.getString("phone"));
        f.setDepartmentId(rs.getInt("department_id"));
        f.setDesignation(rs.getString("designation"));
        f.setQualification(rs.getString("qualification"));
        f.setSpecialization(rs.getString("specialization"));
        Date joining = rs.getDate("joining_date");
        if (joining != null) f.setJoiningDate(joining.toLocalDate());
        f.setAddress(rs.getString("address"));
        f.setCity(rs.getString("city"));
        f.setState(rs.getString("state"));
        f.setPincode(rs.getString("pincode"));
        f.setProfileImage(rs.getString("profile_image"));
        f.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) f.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) f.setUpdatedAt(updated.toLocalDateTime());
        return f;
    }
}
