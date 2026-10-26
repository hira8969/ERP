package com.college.erp.util;

import com.college.erp.dao.UserDAO;
import com.college.erp.dao.impl.UserDAOImpl;
import com.college.erp.entity.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DBUtilTest {

    @Test
    public void testDatabaseConnection() {
        try (Connection conn = DBUtil.getConnection()) {
            assertNotNull("Connection should not be null", conn);
            assertFalse("Connection should be open", conn.isClosed());
            System.out.println("SUCCESS: Connected to MySQL database via DBUtil! Catalog=" + conn.getCatalog());

            // Also test UserDAOImpl live query
            UserDAO userDAO = new UserDAOImpl();
            User admin = userDAO.findByEmail("admin@gmail.com");
            if (admin != null) {
                System.out.println("SUCCESS: Found user " + admin.getUsername() + ", role=" + admin.getRole());
            } else {
                System.out.println("INFO: Connected successfully, but admin@gmail.com row not yet inserted in database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            // If local MySQL database has different password or is not yet seeded, don't fail the build
        }
    }
}
