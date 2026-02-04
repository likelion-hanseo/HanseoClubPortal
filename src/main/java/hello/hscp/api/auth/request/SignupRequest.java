// src/main/java/hello/hscp/api/auth/request/SignupRequest.java
package hello.hscp.api.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Size(max = 50) String loginId,
        @NotBlank @Size(min = 4, max = 100) String password
) {}
