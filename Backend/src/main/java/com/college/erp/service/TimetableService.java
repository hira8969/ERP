package com.college.erp.service;

import com.college.erp.entity.Timetable;
import java.util.List;

public interface TimetableService {

    void saveTimetable(Timetable timetable);

    List<Timetable> getAllTimetables();

    List<Timetable> getTimetableByCourseAndSemester(int courseId, int semesterId);

    List<Timetable> getTimetableByFaculty(int facultyId);

    void deleteTimetable(int timetableId);
}
