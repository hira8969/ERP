package com.college.erp.dao.impl;

import com.college.erp.dao.FacultyDAO;
import com.college.erp.entity.Faculty;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FacultyDAOImpl implements FacultyDAO {

    @Override
    public void saveFaculty(Faculty faculty) {
        executeInTransaction(session -> session.persist(faculty));
    }

    @Override
    public Faculty getFacultyById(int facultyId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Faculty.class, facultyId);
        }
    }

    @Override
    public Faculty getFacultyByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Faculty where userId = :userId", Faculty.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        }
    }

    @Override
    public Faculty getFacultyByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Faculty where email = :email", Faculty.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public List<Faculty> getAllFaculty() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Faculty order by facultyId desc", Faculty.class)
                    .getResultList();
        }
    }

    @Override
    public List<Faculty> getFacultyByDepartment(int departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Faculty where departmentId = :deptId order by firstName", Faculty.class)
                    .setParameter("deptId", departmentId)
                    .getResultList();
        }
    }

    @Override
    public void updateFaculty(Faculty faculty) {
        executeInTransaction(session -> session.merge(faculty));
    }

    @Override
    public void deleteFaculty(int facultyId) {
        executeInTransaction(session -> {
            Faculty faculty = session.get(Faculty.class, facultyId);
            if (faculty != null) {
                session.remove(faculty);
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
