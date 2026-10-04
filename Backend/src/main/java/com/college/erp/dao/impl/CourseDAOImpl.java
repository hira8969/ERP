package com.college.erp.dao.impl;

import com.college.erp.dao.CourseDAO;
import com.college.erp.entity.Course;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public Course findById(int courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Course.class, courseId);
        }
    }

    @Override
    public List<Course> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Course where active = true", Course.class).list();
        }
    }

    @Override
    public List<Course> findByDepartmentId(int departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Course where active = true and departmentId = :deptId", Course.class)
                    .setParameter("deptId", departmentId)
                    .list();
        }
    }

    @Override
    public void save(Course course) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(course);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    @Override
    public void update(Course course) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(course);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    @Override
    public void delete(int courseId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Course c = session.get(Course.class, courseId);
            if (c != null) {
                c.setActive(false);
                session.merge(c);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}
