// src/main/java/hello/hscp/api/common/application/request/SubmitApplicationRequest.java
package hello.hscp.api.common.application.request;

import jakarta.validation.constraints.*;

public record SubmitApplicationRequest(
        @NotBlank @Size(max = 30) String studentNo,
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 50) String department,

        @NotBlank @Size(max = 30) String contact,      // 연락처
        @NotBlank @Size(max = 50) String applyPart,    // 지원파트
        @NotBlank @Size(max = 200) String techStack,   // 기술스택(예: "Java, Spring, MySQL")

        @NotBlank @Size(max = 5000) String motivation
) {}
