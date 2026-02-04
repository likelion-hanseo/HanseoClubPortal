// src/main/java/hello/hscp/api/auth/response/TokenResponse.java
package hello.hscp.api.auth.response;

import hello.hscp.global.security.Role;

public record TokenResponse(
        String accessToken,
        Long userId,
        Role role,
        String loginId
) {}
