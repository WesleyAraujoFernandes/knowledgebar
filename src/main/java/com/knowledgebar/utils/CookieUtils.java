package com.knowledgebar.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class CookieUtils {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private CookieUtils() {
        // utilitária
    }

    /**
     * Obtém o refresh token a partir do cookie.
     */
    public static String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}