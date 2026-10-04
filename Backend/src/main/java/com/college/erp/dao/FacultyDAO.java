package com.college.erp.dao;

import com.college.erp.entity.Faculty;
import java.util.List;

public interface FacultyDAO {

    void saveFaculty(Faculty faculty);

    Faculty getFacultyById(int facultyId);

    Faculty getFacultyByUserId(int userId);

    Faculty getFacultyByEmail(String email);

    List<Faculty> getAllFaculty();

    List<Faculty> getFacultyByDepartment(int departmentId);

    void updateFaculty(Faculty faculty);

    void deleteFaculty(int facultyId);
}
