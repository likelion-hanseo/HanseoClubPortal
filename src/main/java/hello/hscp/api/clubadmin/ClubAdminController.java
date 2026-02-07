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

import java.time.LocalDate;
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
    // 생성: 글만 (raw JSON)
    // POST /api/clubadmin/clubs
    // =========================
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpsertClubResponse createText(@RequestBody ClubUpsertRequest data) {
        validate(data);
        validateRecruitDates(data.recruitStartAt(), data.recruitEndAt());

        Long clubId = clubCommandService.create(
                data.name(),
                data.summary(),
                data.category(),
                data.recruitStartAt(),
                data.recruitEndAt(),
                data.introduction(),
                data.interviewProcess()
        );
        return new UpsertClubResponse(clubId);
    }

    // =========================
    // 생성: 사진만 (multipart)
    // POST /api/clubadmin/clubs?clubId=1
    // =========================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpsertClubResponse uploadMainImageOnCreateEndpoint(
            @RequestParam("clubId") Long clubId,
            @RequestPart("mainImage") MultipartFile mainImage
    ) {
        if (clubId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "clubId is required");
        }
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "mainImage is required");
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        mediaCommandService.replaceAll(club, mainImage, null);
        return new UpsertClubResponse(clubId);
    }

    // =========================
    // 수정: 글만 (raw JSON)
    // PUT /api/clubadmin/clubs/{clubId}
    // =========================
    @PutMapping(value = "/{clubId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpsertClubResponse updateText(
            @PathVariable Long clubId,
            @RequestBody ClubUpsertRequest data
    ) {
        validate(data);
        validateRecruitDates(data.recruitStartAt(), data.recruitEndAt());

        clubCommandService.updateText(
                clubId,
                data.name(),
                data.summary(),
                data.category(),
                data.recruitStartAt(),
                data.recruitEndAt(),
                data.introduction(),
                data.interviewProcess()
        );
        return new UpsertClubResponse(clubId);
    }

    // =========================
    // 수정: 사진만 (multipart)
    // PUT /api/clubadmin/clubs/main-image?clubId=1
    // =========================
    @PutMapping(value = "/main-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpsertClubResponse updateMainImage(
            @RequestParam("clubId") Long clubId,
            @RequestPart("mainImage") MultipartFile mainImage
    ) {
        if (clubId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "clubId is required");
        }
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "mainImage is required");
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

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

    // 날짜만 + null 허용
    private void validateRecruitDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) return;          // 둘 중 하나라도 null이면 허용
        if (end.isBefore(start)) {                         // 같은 날짜는 허용, end < start만 금지
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "recruitEndAt must be >= recruitStartAt");
        }
    }
}
