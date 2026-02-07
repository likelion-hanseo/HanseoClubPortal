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

import java.time.LocalDate;

@Service
public class ClubCommandService {

    private final ClubRepository clubRepository;
    private final MediaCommandService mediaCommandService;

    public ClubCommandService(
            ClubRepository clubRepository,
            MediaCommandService mediaCommandService
    ) {
        this.clubRepository = clubRepository;
        this.mediaCommandService = mediaCommandService;
    }

    @Transactional
    public Long create(
            String name,
            String summary,
            ClubCategory category,
            LocalDate recruitStartAt,
            LocalDate recruitEndAt,
            String introduction,
            String interviewProcess
    ) {
        validateRecruitDates(recruitStartAt, recruitEndAt);

        Club club = new Club(
                name, summary, category,
                recruitStartAt, recruitEndAt,
                introduction, interviewProcess
        );
        clubRepository.save(club);
        return club.getId();
    }

    @Transactional
    public void updateText(
            Long clubId,
            String name,
            String summary,
            ClubCategory category,
            LocalDate recruitStartAt,
            LocalDate recruitEndAt,
            String introduction,
            String interviewProcess
    ) {
        validateRecruitDates(recruitStartAt, recruitEndAt);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        club.update(
                name, summary, category,
                recruitStartAt, recruitEndAt,
                introduction, interviewProcess
        );
    }

    @Transactional
    public void delete(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        // 지원(Application)은 더 이상 club과 무관하므로 삭제하지 않음
        mediaCommandService.deleteAllByClubId(clubId);
        clubRepository.delete(club);
    }

    private void validateRecruitDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) return;
        if (end.isBefore(start)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "recruitEndAt must be >= recruitStartAt");
        }
    }
}
