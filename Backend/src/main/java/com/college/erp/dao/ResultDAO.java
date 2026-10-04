package com.college.erp.dao;

import com.college.erp.entity.Result;
import java.util.List;

public interface ResultDAO {

    void saveResult(Result result);

    Result getResultById(int resultId);

    List<Result> getResultsByStudent(int studentId);

    List<Result> getResultsBySubject(int subjectId);

    List<Result> getAllResults();

    void updateResult(Result result);

    void deleteResult(int resultId);
}
