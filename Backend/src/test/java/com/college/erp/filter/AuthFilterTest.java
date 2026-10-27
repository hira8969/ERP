package com.college.erp.filter;

import com.college.erp.entity.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthFilterTest {

    private AuthFilter authFilter;
    private User adminUser;
    private User facultyUser;
    private User studentUser;

    @Before
    public void setUp() {
        authFilter = new AuthFilter();

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");

        facultyUser = new User();
        facultyUser.setUsername("prof_sharma");
        facultyUser.setRole("FACULTY");

        studentUser = new User();
        studentUser.setUsername("240714100123");
        studentUser.setRole("STUDENT");
    }

    @Test
    public void testAdminUnrestrictedAccess() {
        assertTrue(authFilter.hasAccess(adminUser, "/dashboard.html", "GET"));
        assertTrue(authFilter.hasAccess(adminUser, "/faculty-list.html", "GET"));
        assertTrue(authFilter.hasAccess(adminUser, "/add-faculty.html", "GET"));
        assertTrue(authFilter.hasAccess(adminUser, "/student-list.html", "GET"));
        assertTrue(authFilter.hasAccess(adminUser, "/add-student.html", "GET"));
        assertTrue(authFilter.hasAccess(adminUser, "/users.html", "GET"));
        assertTrue(authFilter.hasAccess(adminUser, "/api/users", "POST"));
        assertTrue(authFilter.hasAccess(adminUser, "/api/attendance", "POST"));
        assertTrue(authFilter.hasAccess(adminUser, "/students/delete", "POST"));
    }

    @Test
    public void testFacultyAccess() {
        // Allowed
        assertTrue(authFilter.hasAccess(facultyUser, "/dashboard.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/faculty-list.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/student-list.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/student-details.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/attendance.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/results.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/timetable.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/courses.html", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/notices.html", "GET"));

        // Faculty API permissions
        assertTrue(authFilter.hasAccess(facultyUser, "/api/attendance", "POST")); // Mark attendance
        assertTrue(authFilter.hasAccess(facultyUser, "/api/results", "POST"));    // Record marks
        assertTrue(authFilter.hasAccess(facultyUser, "/api/notices", "POST"));    // Post announcement
        assertTrue(authFilter.hasAccess(facultyUser, "/api/students", "GET"));
        assertTrue(authFilter.hasAccess(facultyUser, "/api/faculty", "GET"));

        // Blocked for Faculty
        assertFalse(authFilter.hasAccess(facultyUser, "/users.html", "GET"));
        assertFalse(authFilter.hasAccess(facultyUser, "/api/users", "GET"));
        assertFalse(authFilter.hasAccess(facultyUser, "/add-student.html", "GET"));
        assertFalse(authFilter.hasAccess(facultyUser, "/add-faculty.html", "GET"));
        assertFalse(authFilter.hasAccess(facultyUser, "/students/delete", "POST"));
        assertFalse(authFilter.hasAccess(facultyUser, "/api/students", "POST"));
    }

    @Test
    public void testStudentAccess() {
        // Allowed read-only pages (including faculty-list.html which was previously 403)
        assertTrue(authFilter.hasAccess(studentUser, "/dashboard.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/faculty-list.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/student-list.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/student-details.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/attendance.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/results.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/timetable.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/courses.html", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/notices.html", "GET"));

        // Allowed APIs (GET only)
        assertTrue(authFilter.hasAccess(studentUser, "/api/faculty", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/api/students", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/api/attendance", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/api/results", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/api/timetables", "GET"));
        assertTrue(authFilter.hasAccess(studentUser, "/api/dashboard/stats", "GET"));

        // Strictly blocked for Student
        assertFalse(authFilter.hasAccess(studentUser, "/api/attendance", "POST")); // Cannot mark attendance!
        assertFalse(authFilter.hasAccess(studentUser, "/api/results", "POST"));    // Cannot enter marks!
        assertFalse(authFilter.hasAccess(studentUser, "/api/notices", "POST"));    // Cannot post notices!
        assertFalse(authFilter.hasAccess(studentUser, "/users.html", "GET"));      // Cannot view credentials!
        assertFalse(authFilter.hasAccess(studentUser, "/api/users", "GET"));
        assertFalse(authFilter.hasAccess(studentUser, "/add-student.html", "GET"));
        assertFalse(authFilter.hasAccess(studentUser, "/add-faculty.html", "GET"));
        assertFalse(authFilter.hasAccess(studentUser, "/students/delete", "POST"));
    }
}
