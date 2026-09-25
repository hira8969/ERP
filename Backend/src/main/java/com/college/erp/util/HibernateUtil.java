package com.college.erp.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try (InputStream inputStream = HibernateUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("db.properties was not found");
            }

            Properties databaseProperties = new Properties();
            databaseProperties.load(inputStream);

            Configuration configuration = new Configuration().configure();
            configuration.setProperty("hibernate.connection.driver_class",
                    databaseProperties.getProperty("db.driver"));
            configuration.setProperty("hibernate.connection.url",
                    databaseProperties.getProperty("db.url"));
            configuration.setProperty("hibernate.connection.username",
                    databaseProperties.getProperty("db.username"));
            configuration.setProperty("hibernate.connection.password",
                    databaseProperties.getProperty("db.password"));
            return configuration.buildSessionFactory();
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}