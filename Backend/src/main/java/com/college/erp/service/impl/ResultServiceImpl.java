package com.college.erp.service.impl;

import com.college.erp.dao.ResultDAO;
import com.college.erp.dao.impl.ResultDAOImpl;
import com.college.erp.entity.Result;
import com.college.erp.service.ResultService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ResultServiceImpl implements ResultService {

    private final ResultDAO resultDAO;

    public ResultServiceImpl() {
        this.resultDAO = new ResultDAOImpl();
    }

    public ResultServiceImpl(ResultDAO resultDAO) {
        this.resultDAO = resultDAO;
    }

    @Override
    public void recordResult(Result result) {
        if (result == null) {
            throw new IllegalArgumentException("Result details cannot be empty");
        }
        computeGradeAndStatus(result);
        resultDAO.saveResult(result);
    }

    @Override
    public Result getResultById(int resultId) {
        return resultDAO.getResultById(resultId);
    }

    @Override
    public List<Result> getResultsByStudent(int studentId) {
        return resultDAO.getResultsByStudent(studentId);
    }

    @Override
    public List<Result> getResultsBySubject(int subjectId) {
        return resultDAO.getResultsBySubject(subjectId);
    }

    @Override
    public void updateResult(Result result) {
        if (result == null || result.getResultId() <= 0) {
            throw new IllegalArgumentException("Valid result ID required for update");
        }
        computeGradeAndStatus(result);
        resultDAO.updateResult(result);
    }

    @Override
    public void deleteResult(int resultId) {
        resultDAO.deleteResult(resultId);
    }

    @Override
    public StudentGradeReport getStudentGradeReport(int studentId) {
        List<Result> results = resultDAO.getResultsByStudent(studentId);
        if (results == null || results.isEmpty()) {
            return new StudentGradeReport(studentId, results, 0.0, 0.0, 0.0, 0.0, "N/A");
        }

        double totalMarks = 0.0;
        double totalMax = results.size() * 100.0;
        double totalGradePoints = 0.0;
        boolean hasFailed = false;

        for (Result r : results) {
            if (r.getMarksObtained() != null) {
                totalMarks += r.getMarksObtained().doubleValue();
            }
            if (r.getGradePoint() != null) {
                totalGradePoints += r.getGradePoint().doubleValue();
            }
            if ("FAIL".equalsIgnoreCase(r.getResultStatus())) {
                hasFailed = true;
            }
        }

        double percentage = totalMax > 0 ? (totalMarks / totalMax) * 100.0 : 0.0;
        percentage = Math.round(percentage * 10.0) / 10.0;

        double sgpa = results.size() > 0 ? totalGradePoints / results.size() : 0.0;
        sgpa = Math.round(sgpa * 100.0) / 100.0;

        String overallStatus = hasFailed ? "FAIL" : (sgpa >= 8.5 ? "First Class with Distinction" : (sgpa >= 6.5 ? "First Class" : "Second Class"));

        return new StudentGradeReport(studentId, results, totalMarks, totalMax, percentage, sgpa, overallStatus);
    }

    private void computeGradeAndStatus(Result result) {
        if (result.getMarksObtained() == null) {
            result.setMarksObtained(BigDecimal.ZERO);
        }
        double marks = result.getMarksObtained().doubleValue();

        if (marks >= 90.0) {
            result.setGrade("O");
            result.setGradePoint(new BigDecimal("10.00"));
            result.setResultStatus("PASS");
        } else if (marks >= 80.0) {
            result.setGrade("A+");
            result.setGradePoint(new BigDecimal("9.00"));
            result.setResultStatus("PASS");
        } else if (marks >= 70.0) {
            result.setGrade("A");
            result.setGradePoint(new BigDecimal("8.00"));
            result.setResultStatus("PASS");
        } else if (marks >= 60.0) {
            result.setGrade("B+");
            result.setGradePoint(new BigDecimal("7.00"));
            result.setResultStatus("PASS");
        } else if (marks >= 50.0) {
            result.setGrade("B");
            result.setGradePoint(new BigDecimal("6.00"));
            result.setResultStatus("PASS");
        } else if (marks >= 40.0) {
            result.setGrade("C");
            result.setGradePoint(new BigDecimal("5.00"));
            result.setResultStatus("PASS");
        } else {
            result.setGrade("F");
            result.setGradePoint(new BigDecimal("0.00"));
            result.setResultStatus("FAIL");
        }
    }
}
