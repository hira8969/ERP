package com.college.erp.service.impl;

import com.college.erp.dao.TimetableDAO;
import com.college.erp.dao.impl.TimetableDAOImpl;
import com.college.erp.entity.Timetable;
import com.college.erp.service.TimetableService;

import java.util.List;

public class TimetableServiceImpl implements TimetableService {

    private final TimetableDAO timetableDAO;

    public TimetableServiceImpl() {
        this.timetableDAO = new TimetableDAOImpl();
    }

    public TimetableServiceImpl(TimetableDAO timetableDAO) {
        this.timetableDAO = timetableDAO;
    }

    @Override
    public void saveTimetable(Timetable timetable) {
        if (timetable == null) {
            throw new IllegalArgumentException("Timetable schedule cannot be empty");
        }
        timetableDAO.saveTimetable(timetable);
    }

    @Override
    public List<Timetable> getAllTimetables() {
        return timetableDAO.getAllTimetables();
    }

    @Override
    public List<Timetable> getTimetableByCourseAndSemester(int courseId, int semesterId) {
        return timetableDAO.getTimetableByCourseAndSemester(courseId, semesterId);
    }

    @Override
    public List<Timetable> getTimetableByFaculty(int facultyId) {
        return timetableDAO.getTimetableByFaculty(facultyId);
    }

    @Override
    public void deleteTimetable(int timetableId) {
        timetableDAO.deleteTimetable(timetableId);
    }
}
