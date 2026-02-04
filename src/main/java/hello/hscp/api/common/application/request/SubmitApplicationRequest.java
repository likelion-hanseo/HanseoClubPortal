// src/main/java/hello/hscp/api/common/application/request/SubmitApplicationRequest.java
package hello.hscp.api.common.application.request;

import jakarta.validation.constraints.*;

public record SubmitApplicationRequest(
        @NotBlank @Size(max = 30) String studentNo,
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 50) String department,
        @Min(1) @Max(200) int age,
        @Min(1) @Max(10) int grade,
        @NotBlank @Size(max = 5000) String motivation
) {}
