// src/main/java/hello/hscp/domain/account/service/RefreshTokenCookieService.java
package hello.hscp.domain.account.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCookieService {

    @Value("${app.security.refresh-cookie-name:REFRESH_TOKEN}")
    private String cookieName;

    @Value("${app.security.refresh-cookie-domain:}")
    private String cookieDomain;

    @Value("${app.security.refresh-cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${app.security.refresh-cookie-same-site:Lax}")
    private String cookieSameSite;

    @Value("${app.jwt.refresh-ttl-seconds:2592000}")
    private long refreshTtlSeconds;

    public String read(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    public void set(HttpServletResponse res, String refreshToken) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshTtlSeconds)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            b.domain(cookieDomain);
        }

        res.addHeader("Set-Cookie", b.build().toString());
    }

    public void clear(HttpServletResponse res) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            b.domain(cookieDomain);
        }

        res.addHeader("Set-Cookie", b.build().toString());
    }

    public String cookieName() {
        return cookieName;
    }
}
