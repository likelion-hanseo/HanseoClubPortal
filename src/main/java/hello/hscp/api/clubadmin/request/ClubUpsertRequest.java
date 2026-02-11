// src/main/java/hello/hscp/api/clubadmin/request/ClubUpsertRequest.java
package hello.hscp.api.clubadmin.request;

import hello.hscp.domain.club.entity.ClubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClubUpsertRequest(
        @NotBlank String name,
        @NotBlank String summary,

        // 날짜만 받고, null 허용
        LocalDate recruitStartAt,
        LocalDate recruitEndAt,

        @NotNull ClubCategory category,
        @NotBlank String introduction,
        @NotBlank String interviewProcess
) {}
