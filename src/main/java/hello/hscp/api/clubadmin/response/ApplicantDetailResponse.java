// src/main/java/hello/hscp/api/clubadmin/response/ApplicantDetailResponse.java
package hello.hscp.api.clubadmin.response;

import java.time.LocalDateTime;

public record ApplicantDetailResponse(
        Long applicationId,
        String studentNo,
        String name,
        String department,
        String contact,
        String applyPart,
        String techStack,
        String motivation,
        LocalDateTime createdAt
) {}
