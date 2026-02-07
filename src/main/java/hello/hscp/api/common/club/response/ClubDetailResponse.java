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

        // 상세 조회 시점 기준 모집 마감까지 남은 일수 (end null이면 null)
        Long daysLeftToRecruitEnd,

        long viewCount,
        ClubCategory category,
        String introduction,
        String interviewProcess
) {}
