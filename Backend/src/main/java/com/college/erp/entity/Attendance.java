package com.college.erp.entity;

import java.time.LocalDate;

public class Attendance {

    private int attendanceId;
    private int studentId;
    private int subjectId;
    private int facultyId;
    private LocalDate attendanceDate;
    private String status;
    private String remarks;

    public Attendance() {
    }

    public Attendance(int studentId, int subjectId, int facultyId, LocalDate attendanceDate, String status, String remarks) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.facultyId = facultyId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}