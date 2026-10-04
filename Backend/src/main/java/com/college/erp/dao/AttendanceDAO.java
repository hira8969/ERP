package com.college.erp.dao;

import com.college.erp.entity.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceDAO {

    void saveAttendance(Attendance attendance);

    void saveBatchAttendance(List<Attendance> list);

    List<Attendance> getAttendanceByStudent(int studentId);

    List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date);

    List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId);

    List<Attendance> getAllAttendance();
}
