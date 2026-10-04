package com.college.erp.service;

import com.college.erp.dao.ResultDAO;
import com.college.erp.entity.Result;
import com.college.erp.service.impl.ResultServiceImpl;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ResultServiceTest {

    @Test
    public void testRecordResultGradesCalculation() {
        List<Result> stored = new ArrayList<>();
        ResultDAO mockDao = new ResultDAO() {
            @Override public void saveResult(Result result) { stored.add(result); }
            @Override public Result getResultById(int resultId) { return null; }
            @Override public List<Result> getResultsByStudent(int studentId) { return stored; }
            @Override public List<Result> getResultsBySubject(int subjectId) { return List.of(); }
            @Override public List<Result> getAllResults() { return stored; }
            @Override public void updateResult(Result result) {}
            @Override public void deleteResult(int resultId) {}
        };

        ResultService service = new ResultServiceImpl(mockDao);

        // Score 92 -> Grade O, Point 10.00
        Result r1 = new Result();
        r1.setStudentId(1);
        r1.setSubjectId(1);
        r1.setMarksObtained(new BigDecimal("92.00"));
        service.recordResult(r1);

        assertEquals("O", r1.getGrade());
        assertEquals(new BigDecimal("10.00"), r1.getGradePoint());
        assertEquals("PASS", r1.getResultStatus());

        // Score 85 -> Grade A+, Point 9.00
        Result r2 = new Result();
        r2.setStudentId(1);
        r2.setSubjectId(2);
        r2.setMarksObtained(new BigDecimal("85.00"));
        service.recordResult(r2);

        assertEquals("A+", r2.getGrade());
        assertEquals(new BigDecimal("9.00"), r2.getGradePoint());

        // Grade report SGPA
        ResultService.StudentGradeReport report = service.getStudentGradeReport(1);
        assertEquals(2, report.getResults().size());
        assertEquals(9.5, report.getSgpa(), 0.01);
        assertEquals("First Class with Distinction", report.getOverallStatus());
    }
}
