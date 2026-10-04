package com.college.erp.dao.impl;

import com.college.erp.dao.NoticeDAO;
import com.college.erp.entity.Notice;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class NoticeDAOImpl implements NoticeDAO {

    @Override
    public void saveNotice(Notice notice) {
        executeInTransaction(session -> session.persist(notice));
    }

    @Override
    public List<Notice> getAllNotices() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Notice where active = true order by publishDate desc", Notice.class)
                    .getResultList();
        }
    }

    @Override
    public List<Notice> getNoticesByRole(String role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Notice where active = true and (targetRole = 'ALL' or targetRole = :role) order by publishDate desc", Notice.class)
                    .setParameter("role", role)
                    .getResultList();
        }
    }

    @Override
    public Notice getNoticeById(int noticeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Notice.class, noticeId);
        }
    }

    @Override
    public void deleteNotice(int noticeId) {
        executeInTransaction(session -> {
            Notice notice = session.get(Notice.class, noticeId);
            if (notice != null) {
                session.remove(notice);
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
