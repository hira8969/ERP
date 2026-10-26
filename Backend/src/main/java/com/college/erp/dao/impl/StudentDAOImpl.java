package com.college.erp.dao.impl;

import com.college.erp.dao.StudentDAO;
import com.college.erp.entity.Student;
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

public class StudentDAOImpl implements StudentDAO {

    private static final String SELECT_COLUMNS = "student_id, user_id, admission_number, roll_number, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, pincode, department_id, course_id, semester_id, admission_date, guardian_name, guardian_phone, blood_group, profile_image, status, created_at, updated_at";

    @Override
    public void saveStudent(Student s) {
        String sql = "INSERT INTO students (user_id, admission_number, roll_number, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, pincode, department_id, course_id, semester_id, admission_date, guardian_name, guardian_phone, blood_group, profile_image, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStudentParams(stmt, s);
            stmt.setTimestamp(23, s.getCreatedAt() != null ? Timestamp.valueOf(s.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(24, s.getUpdatedAt() != null ? Timestamp.valueOf(s.getUpdatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setStudentId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving student: " + e.getMessage(), e);
        }
    }

    @Override
    public Student getStudentById(int studentId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM students WHERE student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding student by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM students ORDER BY student_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all students: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateStudent(Student s) {
        String sql = "UPDATE students SET user_id=?, admission_number=?, roll_number=?, first_name=?, last_name=?, gender=?, date_of_birth=?, email=?, phone=?, address=?, city=?, state=?, pincode=?, department_id=?, course_id=?, semester_id=?, admission_date=?, guardian_name=?, guardian_phone=?, blood_group=?, profile_image=?, status=?, updated_at=? WHERE student_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStudentParams(stmt, s);
            stmt.setTimestamp(23, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(24, s.getStudentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Student was not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student: " + e.getMessage(), e);
        }
    }

    private void setStudentParams(PreparedStatement stmt, Student s) throws SQLException {
        stmt.setInt(1, s.getUserId());
        stmt.setString(2, s.getAdmissionNumber());
        stmt.setString(3, s.getRollNumber());
        stmt.setString(4, s.getFirstName());
        stmt.setString(5, s.getLastName());
        stmt.setString(6, s.getGender());
        stmt.setDate(7, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
        stmt.setString(8, s.getEmail());
        stmt.setString(9, s.getPhone());
        stmt.setString(10, s.getAddress());
        stmt.setString(11, s.getCity());
        stmt.setString(12, s.getState());
        stmt.setString(13, s.getPincode());
        stmt.setInt(14, s.getDepartmentId());
        stmt.setInt(15, s.getCourseId());
        stmt.setInt(16, s.getSemesterId());
        stmt.setDate(17, s.getAdmissionDate() != null ? Date.valueOf(s.getAdmissionDate()) : null);
        stmt.setString(18, s.getGuardianName());
        stmt.setString(19, s.getGuardianPhone());
        stmt.setString(20, s.getBloodGroup());
        stmt.setString(21, s.getProfileImage());
        stmt.setString(22, s.getStatus() != null ? s.getStatus() : "ACTIVE");
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setAdmissionNumber(rs.getString("admission_number"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setGender(rs.getString("gender"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) s.setDateOfBirth(dob.toLocalDate());
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        s.setCity(rs.getString("city"));
        s.setState(rs.getString("state"));
        s.setPincode(rs.getString("pincode"));
        s.setDepartmentId(rs.getInt("department_id"));
        s.setCourseId(rs.getInt("course_id"));
        s.setSemesterId(rs.getInt("semester_id"));
        Date admDate = rs.getDate("admission_date");
        if (admDate != null) s.setAdmissionDate(admDate.toLocalDate());
        s.setGuardianName(rs.getString("guardian_name"));
        s.setGuardianPhone(rs.getString("guardian_phone"));
        s.setBloodGroup(rs.getString("blood_group"));
        s.setProfileImage(rs.getString("profile_image"));
        s.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) s.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) s.setUpdatedAt(updated.toLocalDateTime());
        return s;
    }
}