// src/main/java/hello/hscp/domain/club/service/ClubQueryService.java
package hello.hscp.domain.club.service;

import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.club.entity.RecruitState;
import hello.hscp.domain.club.repository.ClubRepository;
import hello.hscp.domain.media.service.MediaQueryService;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClubQueryService {

    private final ClubRepository clubRepository;
    private final MediaQueryService mediaQueryService;

    public ClubQueryService(ClubRepository clubRepository, MediaQueryService mediaQueryService) {
        this.clubRepository = clubRepository;
        this.mediaQueryService = mediaQueryService;
    }

    // status=null -> 전체, status=UNKNOWN -> 모집기간 null인 것만
    @Transactional(readOnly = true)
    public List<Club> searchPublic(String q, RecruitState status) {
        LocalDate today = LocalDate.now();
        String statusName = (status == null) ? null : status.name();
        return clubRepository.searchPublic(q, statusName, today);
    }

    @Transactional
    public Club getDetailAndIncreaseView(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));
        club.increaseViewCount();
        return club;
    }

    @Transactional(readOnly = true)
    public String mainImageUrl(Long clubId) {
        return mediaQueryService.mainImageUrl(clubId);
    }
}
