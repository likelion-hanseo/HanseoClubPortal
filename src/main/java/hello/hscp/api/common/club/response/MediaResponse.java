// src/main/java/hello/hscp/api/common/club/response/MediaResponse.java
package hello.hscp.api.common.club.response;

import hello.hscp.domain.media.entity.MediaType;

public record MediaResponse(
        Long mediaId,
        MediaType type,
        String url,
        boolean isMain
) {}
