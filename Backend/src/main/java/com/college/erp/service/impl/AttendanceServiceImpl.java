package com.college.erp.service.impl;

import com.college.erp.dao.AttendanceDAO;
import com.college.erp.dao.impl.AttendanceDAOImpl;
import com.college.erp.entity.Attendance;
import com.college.erp.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;

public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceServiceImpl() {
        this.attendanceDAO = new AttendanceDAOImpl();
    }

    public AttendanceServiceImpl(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    @Override
    public void markAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance details cannot be empty");
        }
        LocalDate today = LocalDate.now();
        if (attendance.getAttendanceDate() == null) {
            attendance.setAttendanceDate(today);
        } else if (!attendance.getAttendanceDate().equals(today)) {
            throw new IllegalArgumentException("Attendance can only be marked for today (" + today + "). Past and future dates are strictly prohibited.");
        }
        if (attendance.getStatus() == null || attendance.getStatus().trim().isEmpty()) {
            attendance.setStatus("PRESENT");
        }
        attendanceDAO.saveOrUpdateAttendance(attendance);
    }

    @Override
    public void markBatchAttendance(List<Attendance> list) {
        if (list == null || list.isEmpty()) return;
        LocalDate today = LocalDate.now();
        for (Attendance att : list) {
            if (att.getAttendanceDate() == null) {
                att.setAttendanceDate(today);
            } else if (!att.getAttendanceDate().equals(today)) {
                throw new IllegalArgumentException("Attendance can only be marked for today (" + today + "). Past and future dates are strictly prohibited.");
            }
            if (att.getStatus() == null || att.getStatus().trim().isEmpty()) {
                att.setStatus("PRESENT");
            }
            attendanceDAO.saveOrUpdateAttendance(att);
        }
    }

    @Override
    public void updateAttendance(Attendance attendance) {
        if (attendance == null || attendance.getAttendanceId() <= 0) {
            throw new IllegalArgumentException("Invalid attendance record specified for update");
        }
        if (attendance.getStatus() == null || attendance.getStatus().trim().isEmpty()) {
            attendance.setStatus("PRESENT");
        }
        attendanceDAO.updateAttendance(attendance);
    }

    @Override
    public Attendance getAttendanceById(int attendanceId) {
        return attendanceDAO.getAttendanceById(attendanceId);
    }

    @Override
    public List<Attendance> getAttendanceByStudent(int studentId) {
        return attendanceDAO.getAttendanceByStudent(studentId);
    }

    @Override
    public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date) {
        return attendanceDAO.getAttendanceBySubjectAndDate(subjectId, date);
    }

    @Override
    public AttendanceSummary getStudentAttendanceSummary(int studentId) {
        List<Attendance> records = attendanceDAO.getAttendanceByStudent(studentId);
        int total = records.size();
        int present = 0;
        int absent = 0;

        for (Attendance att : records) {
            if ("PRESENT".equalsIgnoreCase(att.getStatus()) || "LATE".equalsIgnoreCase(att.getStatus())) {
                present++;
            } else {
                absent++;
            }
        }

        double percentage = total > 0 ? ((double) present / total) * 100.0 : 0.0;
        // Standard university criteria: 75% attendance required for examination
        boolean eligible = percentage >= 75.0;

        // Round percentage to 1 decimal place
        percentage = Math.round(percentage * 10.0) / 10.0;

        return new AttendanceSummary(total, present, absent, percentage, eligible);
    }
}
