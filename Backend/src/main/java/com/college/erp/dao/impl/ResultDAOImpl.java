package com.college.erp.dao.impl;

import com.college.erp.dao.ResultDAO;
import com.college.erp.entity.Result;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ResultDAOImpl implements ResultDAO {

    @Override
    public void saveResult(Result result) {
        executeInTransaction(session -> session.persist(result));
    }

    @Override
    public Result getResultById(int resultId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Result.class, resultId);
        }
    }

    @Override
    public List<Result> getResultsByStudent(int studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Result where studentId = :studentId", Result.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<Result> getResultsBySubject(int subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Result where subjectId = :subjectId", Result.class)
                    .setParameter("subjectId", subjectId)
                    .getResultList();
        }
    }

    @Override
    public List<Result> getAllResults() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Result order by resultId desc", Result.class)
                    .getResultList();
        }
    }

    @Override
    public void updateResult(Result result) {
        executeInTransaction(session -> session.merge(result));
    }

    @Override
    public void deleteResult(int resultId) {
        executeInTransaction(session -> {
            Result result = session.get(Result.class, resultId);
            if (result != null) {
                session.remove(result);
            }
        });
    }

    private void executeInTransaction(SessionOperation operation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            operation.execute(session);
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

    @FunctionalInterface
    private interface SessionOperation {
        void execute(Session session);
    }
}
