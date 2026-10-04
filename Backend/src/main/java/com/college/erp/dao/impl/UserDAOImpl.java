package com.college.erp.dao.impl;

import com.college.erp.dao.UserDAO;
import com.college.erp.entity.User;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findByEmail(String email) {
        if (email == null) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User where lower(email) = :email", User.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .uniqueResult();
        }
    }

    @Override
    public User findById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, userId);
        }
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username.trim())
                    .uniqueResult();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User order by userId desc", User.class)
                    .getResultList();
        }
    }

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

    @Override
    public void delete(int userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }
}