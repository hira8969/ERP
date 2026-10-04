package com.college.erp.service;

import com.college.erp.dao.AttendanceDAO;
import com.college.erp.entity.Attendance;
import com.college.erp.service.impl.AttendanceServiceImpl;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AttendanceServiceTest {

    @Test
    public void testAttendanceSummaryEligible() {
        AttendanceDAO mockDao = new AttendanceDAO() {
            @Override public void saveAttendance(Attendance attendance) {}
            @Override public void saveBatchAttendance(List<Attendance> list) {}
            @Override public List<Attendance> getAttendanceByStudent(int studentId) {
                List<Attendance> list = new ArrayList<>();
                // 8 Present out of 10 = 80% (Eligible)
                for (int i = 0; i < 8; i++) {
                    Attendance a = new Attendance();
                    a.setStatus("PRESENT");
                    list.add(a);
                }
                for (int i = 0; i < 2; i++) {
                    Attendance a = new Attendance();
                    a.setStatus("ABSENT");
                    list.add(a);
                }
                return list;
            }
            @Override public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date) { return List.of(); }
            @Override public List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId) { return List.of(); }
            @Override public List<Attendance> getAllAttendance() { return List.of(); }
        };

        AttendanceService service = new AttendanceServiceImpl(mockDao);
        AttendanceService.AttendanceSummary summary = service.getStudentAttendanceSummary(1);

        assertEquals(10, summary.getTotalClasses());
        assertEquals(8, summary.getPresentClasses());
        assertEquals(2, summary.getAbsentClasses());
        assertEquals(80.0, summary.getPercentage(), 0.01);
        assertTrue(summary.isEligibleForExam());
    }

    @Test
    public void testAttendanceSummaryShortage() {
        AttendanceDAO mockDao = new AttendanceDAO() {
            @Override public void saveAttendance(Attendance attendance) {}
            @Override public void saveBatchAttendance(List<Attendance> list) {}
            @Override public List<Attendance> getAttendanceByStudent(int studentId) {
                List<Attendance> list = new ArrayList<>();
                // 6 Present out of 10 = 60% (< 75% -> Not eligible)
                for (int i = 0; i < 6; i++) {
                    Attendance a = new Attendance();
                    a.setStatus("PRESENT");
                    list.add(a);
                }
                for (int i = 0; i < 4; i++) {
                    Attendance a = new Attendance();
                    a.setStatus("ABSENT");
                    list.add(a);
                }
                return list;
            }
            @Override public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date) { return List.of(); }
            @Override public List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId) { return List.of(); }
            @Override public List<Attendance> getAllAttendance() { return List.of(); }
        };

        AttendanceService service = new AttendanceServiceImpl(mockDao);
        AttendanceService.AttendanceSummary summary = service.getStudentAttendanceSummary(2);

        assertEquals(10, summary.getTotalClasses());
        assertEquals(6, summary.getPresentClasses());
        assertEquals(4, summary.getAbsentClasses());
        assertEquals(60.0, summary.getPercentage(), 0.01);
        assertFalse(summary.isEligibleForExam());
    }
}
