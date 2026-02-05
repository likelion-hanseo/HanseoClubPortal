// src/main/java/hello/hscp/domain/club/service/ClubCommandService.java
package hello.hscp.domain.club.service;

import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.club.entity.ClubCategory;
import hello.hscp.domain.club.repository.ClubRepository;
import hello.hscp.domain.media.service.MediaCommandService;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class ClubCommandService {

    private final ClubRepository clubRepository;
    private final MediaCommandService mediaCommandService;

    public ClubCommandService(ClubRepository clubRepository, MediaCommandService mediaCommandService) {
        this.clubRepository = clubRepository;
        this.mediaCommandService = mediaCommandService;
    }

    @Transactional
    public Long create(
            String name,
            String summary,
            ClubCategory category,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
            String introduction,
            String interviewProcess,
            MultipartFile mainImage
    ) {
        validateRecruitPeriod(recruitStartAt, recruitEndAt);

        Club club = new Club(name, summary, category, recruitStartAt, recruitEndAt, introduction, interviewProcess);
        clubRepository.save(club);

        mediaCommandService.replaceMainImage(club, mainImage);
        return club.getId();
    }

    @Transactional
    public void update(
            Long clubId,
            String name,
            String summary,
            ClubCategory category,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
            String introduction,
            String interviewProcess,
            MultipartFile mainImageOrNull
    ) {
        validateRecruitPeriod(recruitStartAt, recruitEndAt);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        club.update(name, summary, category, recruitStartAt, recruitEndAt, introduction, interviewProcess);

        if (mainImageOrNull != null && !mainImageOrNull.isEmpty()) {
            mediaCommandService.replaceMainImage(club, mainImageOrNull);
        }
    }

    @Transactional
    public void delete(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));
        clubRepository.delete(club);
    }

    private void validateRecruitPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "recruitEndAt must be after recruitStartAt");
        }
    }
}
