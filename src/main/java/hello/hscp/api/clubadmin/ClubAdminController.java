// src/main/java/hello/hscp/api/clubadmin/ClubAdminController.java
package hello.hscp.api.clubadmin;

import hello.hscp.api.clubadmin.request.ClubUpsertRequest;
import hello.hscp.api.clubadmin.response.UpsertClubResponse;
import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.club.repository.ClubRepository;
import hello.hscp.domain.club.service.ClubCommandService;
import hello.hscp.domain.media.service.MediaCommandService;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/clubadmin/clubs")
public class ClubAdminController {

    private final ClubCommandService clubCommandService;
    private final ClubRepository clubRepository;
    private final MediaCommandService mediaCommandService;
    private final Validator validator;

    public ClubAdminController(
            ClubCommandService clubCommandService,
            ClubRepository clubRepository,
            MediaCommandService mediaCommandService,
            Validator validator
    ) {
        this.clubCommandService = clubCommandService;
        this.clubRepository = clubRepository;
        this.mediaCommandService = mediaCommandService;
        this.validator = validator;
    }

    // =========================
    // 글(텍스트)만: raw JSON
    // =========================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpsertClubResponse create(@RequestBody ClubUpsertRequest data) {
        validate(data);
        validateRecruitPeriod(data.recruitStartAt(), data.recruitEndAt());

        Club club = new Club(
                data.name(),
                data.summary(),
                data.category(),
                data.recruitStartAt(),
                data.recruitEndAt(),
                data.introduction(),
                data.interviewProcess()
        );
        clubRepository.save(club);

        return new UpsertClubResponse(club.getId());
    }

    @PutMapping(value = "/{clubId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpsertClubResponse updateText(
            @PathVariable Long clubId,
            @RequestBody ClubUpsertRequest data
    ) {
        validate(data);

        // 텍스트만 수정 (미디어 변경 없음)
        clubCommandService.update(
                clubId,
                data.name(),
                data.summary(),
                data.category(),
                data.recruitStartAt(),
                data.recruitEndAt(),
                data.introduction(),
                data.interviewProcess(),
                null,
                null
        );
        return new UpsertClubResponse(clubId);
    }

    // =========================
    // 대표 사진 1장만: multipart + /{clubId}?upload=1
    // =========================
    @PutMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, params = "upload")
    public UpsertClubResponse updateMainImage(
            @PathVariable Long clubId,
            @RequestParam("upload") String upload, // 존재만 강제
            @RequestPart("mainImage") MultipartFile mainImage
    ) {
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "mainImage is required");
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        // 대표 1장만 운용: 기존 전부 삭제 후 mainImage 1장만 저장
        mediaCommandService.replaceAll(club, mainImage, null);

        return new UpsertClubResponse(clubId);
    }

    @DeleteMapping("/{clubId}")
    public void delete(@PathVariable Long clubId) {
        clubCommandService.delete(clubId);
    }

    private void validate(ClubUpsertRequest data) {
        if (data == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Request body is required");
        }
        Set<ConstraintViolation<ClubUpsertRequest>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            ConstraintViolation<ClubUpsertRequest> v = violations.iterator().next();
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    v.getPropertyPath() + " " + v.getMessage()
            );
        }
    }

    private void validateRecruitPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "recruitEndAt must be after recruitStartAt");
        }
    }
}
