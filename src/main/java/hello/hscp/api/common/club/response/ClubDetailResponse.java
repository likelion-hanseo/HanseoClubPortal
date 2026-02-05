// src/main/java/hello/hscp/api/common/club/response/ClubDetailResponse.java
package hello.hscp.api.common.club.response;

import hello.hscp.domain.club.entity.ClubCategory;
import hello.hscp.domain.club.entity.RecruitState;

public record ClubDetailResponse(
        Long clubId,
        String mainImageUrl,
        String name,
        String summary,
        RecruitState recruitState,
        long viewCount,
        ClubCategory category,
        String introduction,
        String interviewProcess
) {}
