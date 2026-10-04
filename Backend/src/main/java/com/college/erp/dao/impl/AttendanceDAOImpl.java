package com.college.erp.dao.impl;

import com.college.erp.dao.AttendanceDAO;
import com.college.erp.entity.Attendance;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public void saveAttendance(Attendance attendance) {
        executeInTransaction(session -> session.persist(attendance));
    }

    @Override
    public void saveBatchAttendance(List<Attendance> list) {
        if (list == null || list.isEmpty()) return;
        executeInTransaction(session -> {
            for (Attendance att : list) {
                session.persist(att);
            }
        });
    }

    @Override
    public List<Attendance> getAttendanceByStudent(int studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Attendance where studentId = :studentId order by attendanceDate desc", Attendance.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Attendance where subjectId = :subjectId and attendanceDate = :attDate", Attendance.class)
                    .setParameter("subjectId", subjectId)
                    .setParameter("attDate", date)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Attendance where studentId = :studentId and subjectId = :subjectId order by attendanceDate desc", Attendance.class)
                    .setParameter("studentId", studentId)
                    .setParameter("subjectId", subjectId)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> getAllAttendance() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Attendance order by attendanceDate desc", Attendance.class)
                    .getResultList();
        }
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
