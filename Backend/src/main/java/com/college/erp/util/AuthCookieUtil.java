package com.college.erp.util;

import com.college.erp.dao.UserDAO;
import com.college.erp.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class AuthCookieUtil {

    public static final String REMEMBER_COOKIE_NAME = "ERP_REMEMBER_TOKEN";
    private static final String COOKIE_SECRET = "CenturionERP_Secret_Key_2026_SecureSessionCookie";
    private static final int SEVEN_DAYS_SECONDS = 7 * 24 * 60 * 60;

    private AuthCookieUtil() {
    }

    public static void setRememberMeCookie(HttpServletResponse response, User user) {
        if (response == null || user == null) {
            return;
        }
        String signature = generateSignature(user);
        String rawToken = user.getUserId() + ":" + signature;
        String encodedToken = Base64.getUrlEncoder().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));

        Cookie cookie = new Cookie(REMEMBER_COOKIE_NAME, encodedToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(SEVEN_DAYS_SECONDS);
        response.addCookie(cookie);
    }

    public static User getRememberedUser(HttpServletRequest request, UserDAO userDAO) {
        if (request == null || userDAO == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (REMEMBER_COOKIE_NAME.equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token == null || token.isBlank()) {
                    continue;
                }
                try {
                    String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
                    int separatorIndex = decoded.indexOf(':');
                    if (separatorIndex == -1) {
                        continue;
                    }
                    int userId = Integer.parseInt(decoded.substring(0, separatorIndex));
                    String clientSignature = decoded.substring(separatorIndex + 1);

                    User user = userDAO.findById(userId);
                    if (user != null && user.isActive()) {
                        String expectedSignature = generateSignature(user);
                        if (expectedSignature.equals(clientSignature)) {
                            return user;
                        }
                    }
                } catch (Exception ignored) {
                    // Invalid cookie token format or corrupted cookie
                }
            }
        }
        return null;
    }

    public static void clearRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
        if (response == null) {
            return;
        }
        Cookie cookie = new Cookie(REMEMBER_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private static String generateSignature(User user) {
        String payload = user.getUserId() + ":" + user.getPassword() + ":" + COOKIE_SECRET;
        return PasswordUtil.hash(payload);
    }
}
