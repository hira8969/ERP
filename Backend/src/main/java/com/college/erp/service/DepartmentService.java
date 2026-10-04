package com.college.erp.service;

import com.college.erp.entity.Department;
import java.util.List;

public interface DepartmentService {

    Department getDepartmentById(int departmentId);

    List<Department> getAllDepartments();

    void saveDepartment(Department department);

    void updateDepartment(Department department);

    void deleteDepartment(int departmentId);
}
