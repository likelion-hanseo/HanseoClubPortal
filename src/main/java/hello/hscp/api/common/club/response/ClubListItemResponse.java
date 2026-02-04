// src/main/java/hello/hscp/api/common/club/response/ClubListItemResponse.java
package hello.hscp.api.common.club.response;

import hello.hscp.domain.club.entity.ClubCategory;

public record ClubListItemResponse(
        Long clubId,
        String coverUrl,
        String name,
        String summary,
        ClubCategory category
) {}
