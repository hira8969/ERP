package com.college.erp.service;

import com.college.erp.entity.Faculty;
import java.util.List;

public interface FacultyService {

    void saveFaculty(Faculty faculty);

    Faculty getFacultyById(int facultyId);

    Faculty getFacultyByUserId(int userId);

    List<Faculty> getAllFaculty();

    List<Faculty> getFacultyByDepartment(int departmentId);

    void updateFaculty(Faculty faculty);

    void deleteFaculty(int facultyId);
}
