package com.college.erp.dao.impl;

import com.college.erp.dao.DepartmentDAO;
import com.college.erp.entity.Department;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public Department findById(int departmentId) {
        String sql = "SELECT department_id, department_code, department_name, description, hod_faculty_id, active FROM departments WHERE department_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDepartment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding department by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Department> findAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, department_code, department_name, description, hod_faculty_id, active FROM departments WHERE active = true ORDER BY department_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapDepartment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching departments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void save(Department department) {
        String sql = "INSERT INTO departments (department_code, department_name, description, hod_faculty_id, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, department.getDepartmentCode());
            stmt.setString(2, department.getDepartmentName());
            stmt.setString(3, department.getDescription());
            if (department.getHodFacultyId() != null) {
                stmt.setInt(4, department.getHodFacultyId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setBoolean(5, department.isActive());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    department.setDepartmentId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving department: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Department department) {
        String sql = "UPDATE departments SET department_code=?, department_name=?, description=?, hod_faculty_id=?, active=? WHERE department_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department.getDepartmentCode());
            stmt.setString(2, department.getDepartmentName());
            stmt.setString(3, department.getDescription());
            if (department.getHodFacultyId() != null) {
                stmt.setInt(4, department.getHodFacultyId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setBoolean(5, department.isActive());
            stmt.setInt(6, department.getDepartmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating department: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int departmentId) {
        String sql = "DELETE FROM departments WHERE department_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting department: " + e.getMessage(), e);
        }
    }

    private Department mapDepartment(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDepartmentId(rs.getInt("department_id"));
        dept.setDepartmentCode(rs.getString("department_code"));
        dept.setDepartmentName(rs.getString("department_name"));
        dept.setDescription(rs.getString("description"));
        int hod = rs.getInt("hod_faculty_id");
        if (!rs.wasNull()) {
            dept.setHodFacultyId(hod);
        }
        dept.setActive(rs.getBoolean("active"));
        return dept;
    }
}
