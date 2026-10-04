package com.college.erp.service.impl;

import com.college.erp.dao.DepartmentDAO;
import com.college.erp.dao.impl.DepartmentDAOImpl;
import com.college.erp.entity.Department;
import com.college.erp.service.DepartmentService;

import java.util.List;

public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentServiceImpl() {
        this.departmentDAO = new DepartmentDAOImpl();
    }

    public DepartmentServiceImpl(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @Override
    public Department getDepartmentById(int departmentId) {
        if (departmentId <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        return departmentDAO.findById(departmentId);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    @Override
    public void saveDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department data is required");
        }
        if (department.getDepartmentCode() == null || department.getDepartmentCode().isBlank()) {
            throw new IllegalArgumentException("Department code is required");
        }
        if (department.getDepartmentName() == null || department.getDepartmentName().isBlank()) {
            throw new IllegalArgumentException("Department name is required");
        }
        department.setActive(true);
        departmentDAO.save(department);
    }

    @Override
    public void updateDepartment(Department department) {
        if (department == null || department.getDepartmentId() <= 0) {
            throw new IllegalArgumentException("Valid department ID is required");
        }
        departmentDAO.update(department);
    }

    @Override
    public void deleteDepartment(int departmentId) {
        if (departmentId <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        departmentDAO.delete(departmentId);
    }
}
