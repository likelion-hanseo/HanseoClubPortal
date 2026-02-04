// src/main/java/hello/hscp/api/clubadmin/response/ApplicantDetailResponse.java
package hello.hscp.api.clubadmin.response;

import java.time.LocalDateTime;

public record ApplicantDetailResponse(
        Long applicationId,
        String studentNo,
        String name,
        String department,
        int age,
        int grade,
        String motivation,
        LocalDateTime createdAt
) {}
