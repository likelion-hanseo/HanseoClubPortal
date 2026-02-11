// src/main/java/hello/hscp/api/auth/request/LoginRequest.java
package hello.hscp.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String loginId,
        @NotBlank String password
) {}
