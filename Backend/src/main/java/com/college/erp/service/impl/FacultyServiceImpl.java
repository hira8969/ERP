package com.college.erp.service.impl;

import com.college.erp.dao.FacultyDAO;
import com.college.erp.dao.impl.FacultyDAOImpl;
import com.college.erp.entity.Faculty;
import com.college.erp.service.FacultyService;

import java.time.LocalDateTime;
import java.util.List;

public class FacultyServiceImpl implements FacultyService {

    private final FacultyDAO facultyDAO;

    public FacultyServiceImpl() {
        this.facultyDAO = new FacultyDAOImpl();
    }

    public FacultyServiceImpl(FacultyDAO facultyDAO) {
        this.facultyDAO = facultyDAO;
    }

    @Override
    public void saveFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new IllegalArgumentException("Faculty information cannot be empty");
        }
        if (faculty.getFirstName() == null || faculty.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty first name is required");
        }
        if (faculty.getEmail() == null || faculty.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty email address is required");
        }
        if (faculty.getStatus() == null || faculty.getStatus().trim().isEmpty()) {
            faculty.setStatus("ACTIVE");
        }
        faculty.setCreatedAt(LocalDateTime.now());
        faculty.setUpdatedAt(LocalDateTime.now());
        facultyDAO.saveFaculty(faculty);
    }

    @Override
    public Faculty getFacultyById(int facultyId) {
        if (facultyId <= 0) {
            throw new IllegalArgumentException("Valid faculty ID required");
        }
        return facultyDAO.getFacultyById(facultyId);
    }

    @Override
    public Faculty getFacultyByUserId(int userId) {
        return facultyDAO.getFacultyByUserId(userId);
    }

    @Override
    public List<Faculty> getAllFaculty() {
        return facultyDAO.getAllFaculty();
    }

    @Override
    public List<Faculty> getFacultyByDepartment(int departmentId) {
        return facultyDAO.getFacultyByDepartment(departmentId);
    }

    @Override
    public void updateFaculty(Faculty faculty) {
        if (faculty == null || faculty.getFacultyId() <= 0) {
            throw new IllegalArgumentException("Valid faculty record required for update");
        }
        faculty.setUpdatedAt(LocalDateTime.now());
        facultyDAO.updateFaculty(faculty);
    }

    @Override
    public void deleteFaculty(int facultyId) {
        if (facultyId <= 0) {
            throw new IllegalArgumentException("Valid faculty ID required");
        }
        facultyDAO.deleteFaculty(facultyId);
    }
}
