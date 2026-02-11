// src/main/java/hello/hscp/api/clubadmin/response/ApplicantSummaryResponse.java
package hello.hscp.api.clubadmin.response;

import java.time.LocalDate;
import java.util.List;

public record ApplicantSummaryResponse(
        Long applicationId,
        String name,
        String department,
        LocalDate appliedDate,      // 날짜만
        List<String> techStackTags  // 프론트에서 "#"+tag 로 표시
) {}
