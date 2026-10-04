package com.college.erp.service;

import com.college.erp.entity.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    void markAttendance(Attendance attendance);

    void markBatchAttendance(List<Attendance> list);

    List<Attendance> getAttendanceByStudent(int studentId);

    List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date);

    AttendanceSummary getStudentAttendanceSummary(int studentId);

    class AttendanceSummary {
        private int totalClasses;
        private int presentClasses;
        private int absentClasses;
        private double percentage;
        private boolean eligibleForExam;

        public AttendanceSummary() {
        }

        public AttendanceSummary(int totalClasses, int presentClasses, int absentClasses, double percentage, boolean eligibleForExam) {
            this.totalClasses = totalClasses;
            this.presentClasses = presentClasses;
            this.absentClasses = absentClasses;
            this.percentage = percentage;
            this.eligibleForExam = eligibleForExam;
        }

        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }

        public int getPresentClasses() { return presentClasses; }
        public void setPresentClasses(int presentClasses) { this.presentClasses = presentClasses; }

        public int getAbsentClasses() { return absentClasses; }
        public void setAbsentClasses(int absentClasses) { this.absentClasses = absentClasses; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }

        public boolean isEligibleForExam() { return eligibleForExam; }
        public void setEligibleForExam(boolean eligibleForExam) { this.eligibleForExam = eligibleForExam; }
    }
}
