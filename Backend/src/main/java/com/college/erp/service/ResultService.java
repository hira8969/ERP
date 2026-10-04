package com.college.erp.service;

import com.college.erp.entity.Result;
import java.math.BigDecimal;
import java.util.List;

public interface ResultService {

    void recordResult(Result result);

    Result getResultById(int resultId);

    List<Result> getResultsByStudent(int studentId);

    List<Result> getResultsBySubject(int subjectId);

    void updateResult(Result result);

    void deleteResult(int resultId);

    StudentGradeReport getStudentGradeReport(int studentId);

    class StudentGradeReport {
        private int studentId;
        private List<Result> results;
        private double totalMarks;
        private double maxMarks;
        private double percentage;
        private double sgpa;
        private String overallStatus;

        public StudentGradeReport() {
        }

        public StudentGradeReport(int studentId, List<Result> results, double totalMarks, double maxMarks, double percentage, double sgpa, String overallStatus) {
            this.studentId = studentId;
            this.results = results;
            this.totalMarks = totalMarks;
            this.maxMarks = maxMarks;
            this.percentage = percentage;
            this.sgpa = sgpa;
            this.overallStatus = overallStatus;
        }

        public int getStudentId() { return studentId; }
        public List<Result> getResults() { return results; }
        public double getTotalMarks() { return totalMarks; }
        public double getMaxMarks() { return maxMarks; }
        public double getPercentage() { return percentage; }
        public double getSgpa() { return sgpa; }
        public String getOverallStatus() { return overallStatus; }
    }
}
