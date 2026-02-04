// src/main/java/hello/hscp/api/auth/AuthController.java
package hello.hscp.api.auth;

import hello.hscp.api.auth.request.LoginRequest;
import hello.hscp.api.auth.request.SignupRequest;
import hello.hscp.api.auth.response.TokenResponse;
import hello.hscp.domain.account.service.AuthService;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.security.refresh-cookie-name:REFRESH_TOKEN}")
    private String refreshCookieName;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public TokenResponse signup(@RequestBody @Valid SignupRequest req, HttpServletResponse res) {
        return authService.signup(req.loginId(), req.password(), res); // refresh 쿠키 심고 access만 응답
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest req, HttpServletResponse res) {
        return authService.login(req.loginId(), req.password(), res); // refresh 쿠키 심고 access만 응답
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(HttpServletRequest req, HttpServletResponse res) {
        String refreshToken = extractCookie(req, refreshCookieName);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_TOKEN, "Refresh cookie missing");
        }
        return authService.refresh(refreshToken, res); // refresh 쿠키 재세팅 + access 재발급(응답)
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse res) {
        authService.logout(res); // refresh 쿠키 삭제
    }

    private String extractCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
