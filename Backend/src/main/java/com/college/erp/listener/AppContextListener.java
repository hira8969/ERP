package com.college.erp.listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Application started
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 1. Application shutdown cleanup

        // 2. Shut down MySQL AbandonedConnectionCleanupThread to prevent thread memory leaks on reload
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Throwable t) {
            sce.getServletContext().log("Error shutting down MySQL AbandonedConnectionCleanupThread: " + t.getMessage());
        }

        // 3. Deregister JDBC drivers loaded by the webapp class loader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    sce.getServletContext().log("Error deregistering JDBC driver " + driver + ": " + e.getMessage());
                }
            }
        }
    }
}
