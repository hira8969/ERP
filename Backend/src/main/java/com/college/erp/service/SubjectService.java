package com.college.erp.service;

import com.college.erp.entity.Subject;
import java.util.List;

public interface SubjectService {

    void saveSubject(Subject subject);

    Subject getSubjectById(int subjectId);

    List<Subject> getAllSubjects();

    List<Subject> getSubjectsByCourseAndSemester(int courseId, int semesterId);

    List<Subject> getSubjectsByFaculty(int facultyId);

    void updateSubject(Subject subject);

    void deleteSubject(int subjectId);
}
