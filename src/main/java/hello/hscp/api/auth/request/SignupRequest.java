// src/main/java/hello/hscp/api/auth/request/SignupRequest.java
package hello.hscp.api.auth.request;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String loginId,
        @NotBlank String password
) {}
