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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClubQueryService {

    private final ClubRepository clubRepository;
    private final MediaQueryService mediaQueryService;

    public ClubQueryService(ClubRepository clubRepository, MediaQueryService mediaQueryService) {
        this.clubRepository = clubRepository;
        this.mediaQueryService = mediaQueryService;
    }

    @Transactional(readOnly = true)
    public List<Club> searchPublic(String q, RecruitState state) {
        LocalDateTime now = LocalDateTime.now();
        return clubRepository.searchPublic(q, state.name(), now);
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
