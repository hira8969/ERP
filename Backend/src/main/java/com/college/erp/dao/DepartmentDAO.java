package com.college.erp.dao;

import com.college.erp.entity.Department;
import java.util.List;

public interface DepartmentDAO {

    Department findById(int departmentId);

    List<Department> findAll();

    void save(Department department);

    void update(Department department);

    void delete(int departmentId);
}

