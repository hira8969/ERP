package com.college.erp.dao.impl;

import com.college.erp.dao.SemesterDAO;
import com.college.erp.entity.Semester;
import com.college.erp.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class SemesterDAOImpl implements SemesterDAO {

    @Override
    public Semester getSemesterById(int semesterId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Semester.class, semesterId);
        }
    }

    @Override
    public List<Semester> getSemestersByCourse(int courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Semester where courseId = :courseId and active = true order by semesterNumber", Semester.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }

    @Override
    public List<Semester> getAllSemesters() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Semester where active = true order by courseId, semesterNumber", Semester.class)
                    .getResultList();
        }
    }
}
