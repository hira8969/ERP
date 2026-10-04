package com.college.erp.dao;

import com.college.erp.entity.Semester;
import java.util.List;

public interface SemesterDAO {

    Semester getSemesterById(int semesterId);

    List<Semester> getSemestersByCourse(int courseId);

    List<Semester> getAllSemesters();
}
