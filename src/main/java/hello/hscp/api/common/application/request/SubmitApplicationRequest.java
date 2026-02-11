// src/main/java/hello/hscp/api/common/application/request/SubmitApplicationRequest.java
package hello.hscp.api.common.application.request;

import jakarta.validation.constraints.*;

public record SubmitApplicationRequest(
        @NotBlank String studentNo,
        @NotBlank String name,
        @NotBlank String department,
        @NotBlank String contact,      // 연락처
        @NotBlank String applyPart,    // 지원파트
        @NotBlank String techStack,   // 기술스택(예: "Java, Spring, MySQL")
        @NotBlank String motivation
) {}
