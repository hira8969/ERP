package com.college.erp.dao.impl;

import com.college.erp.dao.SubjectDAO;
import com.college.erp.entity.Subject;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SubjectDAOImpl implements SubjectDAO {

    @Override
    public void saveSubject(Subject subject) {
        executeInTransaction(session -> session.persist(subject));
    }

    @Override
    public Subject getSubjectById(int subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Subject.class, subjectId);
        }
    }

    @Override
    public List<Subject> getAllSubjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Subject where active = true order by subjectCode", Subject.class)
                    .getResultList();
        }
    }

    @Override
    public List<Subject> getSubjectsByCourseAndSemester(int courseId, int semesterId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Subject where courseId = :courseId and semesterId = :semId and active = true", Subject.class)
                    .setParameter("courseId", courseId)
                    .setParameter("semId", semesterId)
                    .getResultList();
        }
    }

    @Override
    public List<Subject> getSubjectsByFaculty(int facultyId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Subject where facultyId = :facultyId and active = true", Subject.class)
                    .setParameter("facultyId", facultyId)
                    .getResultList();
        }
    }

    @Override
    public void updateSubject(Subject subject) {
        executeInTransaction(session -> session.merge(subject));
    }

    @Override
    public void deleteSubject(int subjectId) {
        executeInTransaction(session -> {
            Subject subject = session.get(Subject.class, subjectId);
            if (subject != null) {
                session.remove(subject);
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
