package com.college.erp.dao;

import com.college.erp.entity.Subject;
import java.util.List;

public interface SubjectDAO {

    void saveSubject(Subject subject);

    Subject getSubjectById(int subjectId);

    List<Subject> getAllSubjects();

    List<Subject> getSubjectsByCourseAndSemester(int courseId, int semesterId);

    List<Subject> getSubjectsByFaculty(int facultyId);

    void updateSubject(Subject subject);

    void deleteSubject(int subjectId);
}
