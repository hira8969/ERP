package com.college.erp.service.impl;

import com.college.erp.dao.SubjectDAO;
import com.college.erp.dao.impl.SubjectDAOImpl;
import com.college.erp.entity.Subject;
import com.college.erp.service.SubjectService;

import java.util.List;

public class SubjectServiceImpl implements SubjectService {

    private final SubjectDAO subjectDAO;

    public SubjectServiceImpl() {
        this.subjectDAO = new SubjectDAOImpl();
    }

    public SubjectServiceImpl(SubjectDAO subjectDAO) {
        this.subjectDAO = subjectDAO;
    }

    @Override
    public void saveSubject(Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        if (subject.getSubjectCode() == null || subject.getSubjectCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required");
        }
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required");
        }
        subject.setActive(true);
        subjectDAO.saveSubject(subject);
    }

    @Override
    public Subject getSubjectById(int subjectId) {
        return subjectDAO.getSubjectById(subjectId);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectDAO.getAllSubjects();
    }

    @Override
    public List<Subject> getSubjectsByCourseAndSemester(int courseId, int semesterId) {
        return subjectDAO.getSubjectsByCourseAndSemester(courseId, semesterId);
    }

    @Override
    public List<Subject> getSubjectsByFaculty(int facultyId) {
        return subjectDAO.getSubjectsByFaculty(facultyId);
    }

    @Override
    public void updateSubject(Subject subject) {
        if (subject == null || subject.getSubjectId() <= 0) {
            throw new IllegalArgumentException("Valid subject record required");
        }
        subjectDAO.updateSubject(subject);
    }

    @Override
    public void deleteSubject(int subjectId) {
        subjectDAO.deleteSubject(subjectId);
    }
}
