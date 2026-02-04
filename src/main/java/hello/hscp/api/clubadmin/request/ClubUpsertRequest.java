// src/main/java/hello/hscp/api/clubadmin/request/ClubUpsertRequest.java
package hello.hscp.api.clubadmin.request;

import hello.hscp.domain.club.entity.ClubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ClubUpsertRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 200) String summary,
        @NotNull LocalDateTime recruitStartAt,
        @NotNull LocalDateTime recruitEndAt,
        @NotNull ClubCategory category,
        @NotBlank String introduction,
        @NotBlank String interviewProcess
) {}
