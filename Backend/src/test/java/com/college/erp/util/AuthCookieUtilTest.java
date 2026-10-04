package com.college.erp.util;

import com.college.erp.dao.UserDAO;
import com.college.erp.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AuthCookieUtilTest {

    @Test
    public void testSetAndGetRememberedUser() {
        User user = new User();
        user.setUserId(42);
        user.setUsername("testuser");
        user.setPassword("secretPasswordHash");
        user.setActive(true);

        List<Cookie> responseCookies = new ArrayList<>();

        HttpServletResponse mockResponse = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class[]{HttpServletResponse.class},
                (proxy, method, args) -> {
                    if ("addCookie".equals(method.getName())) {
                        responseCookies.add((Cookie) args[0]);
                    }
                    return null;
                }
        );

        AuthCookieUtil.setRememberMeCookie(mockResponse, user);

        assertEquals(1, responseCookies.size());
        Cookie authCookie = responseCookies.get(0);
        assertEquals(AuthCookieUtil.REMEMBER_COOKIE_NAME, authCookie.getName());
        assertTrue(authCookie.isHttpOnly());
        assertEquals(7 * 24 * 60 * 60, authCookie.getMaxAge());
        assertNotNull(authCookie.getValue());

        // Now test retrieval with mock request
        HttpServletRequest mockRequest = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    if ("getCookies".equals(method.getName())) {
                        return new Cookie[]{authCookie};
                    }
                    return null;
                }
        );

        UserDAO mockDao = new UserDAO() {
            @Override
            public User findByEmail(String email) { return null; }
            @Override
            public User findById(int userId) {
                if (userId == 42) return user;
                return null;
            }
            @Override
            public User findByUsername(String username) { return null; }
            @Override
            public java.util.List<User> findAll() { return java.util.Collections.emptyList(); }
            @Override
            public void save(User u) {}
            @Override
            public void update(User u) {}
            @Override
            public void delete(int userId) {}
        };

        User found = AuthCookieUtil.getRememberedUser(mockRequest, mockDao);
        assertNotNull(found);
        assertEquals(42, found.getUserId());
        assertEquals("testuser", found.getUsername());
    }

    @Test
    public void testTamperedCookieFails() {
        Cookie tamperedCookie = new Cookie(AuthCookieUtil.REMEMBER_COOKIE_NAME, "dGFtcGVyZWQtdG9rZW4=");
        HttpServletRequest mockRequest = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    if ("getCookies".equals(method.getName())) {
                        return new Cookie[]{tamperedCookie};
                    }
                    return null;
                }
        );

        UserDAO mockDao = new UserDAO() {
            @Override public User findByEmail(String email) { return null; }
            @Override public User findById(int userId) { return null; }
            @Override public User findByUsername(String username) { return null; }
            @Override public java.util.List<User> findAll() { return java.util.Collections.emptyList(); }
            @Override public void save(User u) {}
            @Override public void update(User u) {}
            @Override public void delete(int userId) {}
        };

        User found = AuthCookieUtil.getRememberedUser(mockRequest, mockDao);
        assertNull(found);
    }
}
