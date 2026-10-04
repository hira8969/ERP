package com.college.erp.dao.impl;

import com.college.erp.dao.FeeDAO;
import com.college.erp.entity.Fee;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FeeDAOImpl implements FeeDAO {

    @Override
    public List<Fee> getFeesByStudent(int studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Fee where studentId = :studentId", Fee.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<Fee> getAllFees() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Fee order by feeId desc", Fee.class)
                    .getResultList();
        }
    }

    @Override
    public Fee getFeeById(int feeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Fee.class, feeId);
        }
    }

    @Override
    public void saveFee(Fee fee) {
        executeInTransaction(session -> session.persist(fee));
    }

    @Override
    public void updateFee(Fee fee) {
        executeInTransaction(session -> session.merge(fee));
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
