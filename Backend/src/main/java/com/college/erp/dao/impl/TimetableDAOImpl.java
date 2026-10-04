package com.college.erp.dao.impl;

import com.college.erp.dao.TimetableDAO;
import com.college.erp.entity.Timetable;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TimetableDAOImpl implements TimetableDAO {

    @Override
    public void saveTimetable(Timetable timetable) {
        executeInTransaction(session -> session.persist(timetable));
    }

    @Override
    public List<Timetable> getAllTimetables() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Timetable order by courseId, semesterId, dayOfWeek", Timetable.class)
                    .getResultList();
        }
    }

    @Override
    public List<Timetable> getTimetableByCourseAndSemester(int courseId, int semesterId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Timetable where courseId = :courseId and semesterId = :semId order by dayOfWeek, startTime", Timetable.class)
                    .setParameter("courseId", courseId)
                    .setParameter("semId", semesterId)
                    .getResultList();
        }
    }

    @Override
    public List<Timetable> getTimetableByFaculty(int facultyId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Timetable where facultyId = :facultyId order by dayOfWeek, startTime", Timetable.class)
                    .setParameter("facultyId", facultyId)
                    .getResultList();
        }
    }

    @Override
    public void deleteTimetable(int timetableId) {
        executeInTransaction(session -> {
            Timetable tt = session.get(Timetable.class, timetableId);
            if (tt != null) {
                session.remove(tt);
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
