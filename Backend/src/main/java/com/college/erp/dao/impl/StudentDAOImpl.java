package com.college.erp.dao.impl;

import com.college.erp.dao.StudentDAO;
import com.college.erp.entity.Student;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public void saveStudent(Student student) {
        executeInTransaction(session -> session.persist(student));
    }

    @Override
    public Student getStudentById(int studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, studentId);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Student order by studentId desc", Student.class)
                    .getResultList();
        }
    }

    @Override
    public void updateStudent(Student student) {
        executeInTransaction(session -> session.merge(student));
    }

    @Override
    public void deleteStudent(int studentId) {
        executeInTransaction(session -> {
            Student student = session.get(Student.class, studentId);
            if (student == null) {
                throw new IllegalArgumentException("Student was not found");
            }
            session.remove(student);
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