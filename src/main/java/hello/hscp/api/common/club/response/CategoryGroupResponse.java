// src/main/java/hello/hscp/api/common/club/response/CategoryGroupResponse.java
package hello.hscp.api.common.club.response;

import hello.hscp.domain.club.entity.ClubCategory;

import java.util.List;

public record CategoryGroupResponse(
        ClubCategory category,
        List<ClubListItemResponse> clubs
) {}
