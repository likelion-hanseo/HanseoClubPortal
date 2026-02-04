// src/main/java/hello/hscp/domain/account/service/AuthService.java
package hello.hscp.domain.account.service;

import hello.hscp.api.auth.response.TokenResponse;
import hello.hscp.domain.account.entity.User;
import hello.hscp.domain.account.repository.UserRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import hello.hscp.global.security.JwtTokenProvider;
import hello.hscp.global.security.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenCookieService refreshTokenCookieService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenCookieService refreshTokenCookieService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenCookieService = refreshTokenCookieService;
    }

    // 기존 시그니처 유지(호출부 깨지지 않게). 쿠키 세팅은 안 됨.
    @Transactional
    public TokenResponse signup(String loginId, String password) {
        return signup(loginId, password, null);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String loginId, String password) {
        return login(loginId, password, null);
    }

    // 쿠키에 refresh 심는 실제 메서드
    @Transactional
    public TokenResponse signup(String loginId, String password, HttpServletResponse res) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new ApiException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        String hash = passwordEncoder.encode(password);
        User user = new User(loginId, hash, Role.ADMIN);
        userRepository.save(user);

        return issueTokens(user, res);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String loginId, String password, HttpServletResponse res) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_CREDENTIALS, "Invalid id/password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(ErrorCode.BAD_CREDENTIALS, "Invalid id/password");
        }

        return issueTokens(user, res);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken, HttpServletResponse res) {
        Long userId = jwtTokenProvider.validateAndGetUserIdFromRefreshToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return issueTokens(user, res);
    }

    public void logout(HttpServletResponse res) {
        refreshTokenCookieService.clear(res);
    }

    private TokenResponse issueTokens(User user, HttpServletResponse res) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        if (res != null) {
            refreshTokenCookieService.set(res, refreshToken);
        }

        return new TokenResponse(accessToken, user.getId(), user.getRole(), user.getLoginId());
    }
}
