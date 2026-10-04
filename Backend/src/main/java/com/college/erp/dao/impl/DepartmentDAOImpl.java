package com.college.erp.dao.impl;

import com.college.erp.dao.DepartmentDAO;
import com.college.erp.entity.Department;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public Department findById(int departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Department.class, departmentId);
        }
    }

    @Override
    public List<Department> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Department where active = true", Department.class).list();
        }
    }

    @Override
    public void save(Department department) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(department);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    @Override
    public void update(Department department) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(department);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    @Override
    public void delete(int departmentId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Department dept = session.get(Department.class, departmentId);
            if (dept != null) {
                dept.setActive(false); // soft delete
                session.merge(dept);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw ex;
        }
    }
}
