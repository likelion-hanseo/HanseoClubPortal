// src/main/java/hello/hscp/global/security/JwtAuthenticationFilter.java
package hello.hscp.global.security;

import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Long userId = jwtTokenProvider.getUserId(token);
                var userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (ApiException e) {
                SecurityContextHolder.clearContext();

                // 토큰 흐름에서 USER_NOT_FOUND는 401로 처리(삭제/비활성/유효하지 않은 사용자)
                int status = (e.errorCode() == ErrorCode.USER_NOT_FOUND)
                        ? HttpServletResponse.SC_UNAUTHORIZED
                        : e.errorCode().status().value();

                writeJsonError(response, status, e.errorCode().code(), e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeJsonError(HttpServletResponse response, int status, String error, String message) throws IOException {
        if (response.isCommitted()) return;

        response.resetBuffer();
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        // 최소 JSON (따옴표 escape 간단 처리)
        String safeMsg = message == null ? "" : message.replace("\"", "\\\"");
        response.getWriter().write("{\"error\":\"" + error + "\",\"message\":\"" + safeMsg + "\"}");
        response.flushBuffer();
    }
}