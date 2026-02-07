// src/main/java/hello/hscp/domain/club/service/ClubCommandService.java
package hello.hscp.domain.club.service;

import hello.hscp.domain.application.repository.ApplicationRepository;
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
import java.util.List;

@Service
public class ClubCommandService {

    private final ClubRepository clubRepository;
    private final MediaCommandService mediaCommandService;
    private final ApplicationRepository applicationRepository;

    public ClubCommandService(
            ClubRepository clubRepository,
            MediaCommandService mediaCommandService,
            ApplicationRepository applicationRepository
    ) {
        this.clubRepository = clubRepository;
        this.mediaCommandService = mediaCommandService;
        this.applicationRepository = applicationRepository;
    }

    /**
     * 글(텍스트)만 생성 (대표사진은 별도 업로드 API 사용)
     */
    @Transactional
    public Long create(
            String name,
            String summary,
            ClubCategory category,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
            String introduction,
            String interviewProcess
    ) {
        validateRecruitPeriod(recruitStartAt, recruitEndAt);

        Club club = new Club(name, summary, category, recruitStartAt, recruitEndAt, introduction, interviewProcess);
        clubRepository.save(club);
        return club.getId();
    }

    /**
     * 글(텍스트)만 수정 (대표사진은 별도 업로드 API 사용)
     */
    @Transactional
    public void updateText(
            Long clubId,
            String name,
            String summary,
            ClubCategory category,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
            String introduction,
            String interviewProcess
    ) {
        validateRecruitPeriod(recruitStartAt, recruitEndAt);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        club.update(name, summary, category, recruitStartAt, recruitEndAt, introduction, interviewProcess);
    }

    /**
     * (호환용) 기존 시그니처가 남아있는 호출부가 있으면 컴파일 깨지지 않게 유지.
     * 이제는 사진 파라미터를 받지 않는다. 들어오면 에러로 막는다.
     */
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
            MultipartFile mainImageOrNull,
            List<MultipartFile> mediaFiles
    ) {
        boolean hasMain = mainImageOrNull != null && !mainImageOrNull.isEmpty();
        boolean hasExtras = mediaFiles != null && mediaFiles.stream().anyMatch(f -> f != null && !f.isEmpty());
        if (hasMain || hasExtras) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Image upload is not allowed here. Use PUT /api/clubadmin/clubs/{clubId}?upload=1"
            );
        }
        updateText(clubId, name, summary, category, recruitStartAt, recruitEndAt, introduction, interviewProcess);
    }

    @Transactional
    public void delete(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        applicationRepository.deleteByClub_Id(clubId);
        mediaCommandService.deleteAllByClubId(clubId);
        clubRepository.delete(club);
    }

    private void validateRecruitPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "recruitEndAt must be after recruitStartAt");
        }
    }
}
