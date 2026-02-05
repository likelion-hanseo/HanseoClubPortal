// src/main/java/hello/hscp/api/common/club/ClubPublicController.java
package hello.hscp.api.common.club;

import hello.hscp.api.common.club.response.*;
import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.club.entity.ClubCategory;
import hello.hscp.domain.club.entity.RecruitState;
import hello.hscp.domain.club.service.ClubQueryService;
import hello.hscp.domain.media.entity.MediaFile;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/common/clubs")
public class ClubPublicController {

    private final ClubQueryService clubQueryService;

    public ClubPublicController(ClubQueryService clubQueryService) {
        this.clubQueryService = clubQueryService;
    }

    // 모집상태는 무조건 필수, 이름검색은 선택
    @GetMapping
    public List<CategoryGroupResponse> search(
            @RequestParam("status") @NotNull RecruitState status,
            @RequestParam(value = "q", required = false) String q
    ) {
        List<Club> clubs = clubQueryService.searchPublic(q, status);

        // category별 그룹핑
        Map<ClubCategory, List<ClubListItemResponse>> grouped = new LinkedHashMap<>();
        for (ClubCategory cat : ClubCategory.values()) {
            grouped.put(cat, new ArrayList<>());
        }

        for (Club c : clubs) {
            String coverUrl = clubQueryService.mainImageUrl(c.getId());
            grouped.get(c.getCategory()).add(new ClubListItemResponse(
                    c.getId(), coverUrl, c.getName(), c.getSummary()
            ));
        }

        List<CategoryGroupResponse> res = new ArrayList<>();
        for (var e : grouped.entrySet()) {
            if (!e.getValue().isEmpty()) {
                res.add(new CategoryGroupResponse(e.getKey(), e.getValue()));
            }
        }
        return res;
    }

    @GetMapping("/{clubId}")
    public ClubDetailResponse detail(@PathVariable Long clubId) {
        Club club = clubQueryService.getDetailAndIncreaseView(clubId);

        String mainImageUrl = clubQueryService.mainImageUrl(clubId);
        LocalDateTime now = LocalDateTime.now();
        RecruitState state = club.recruitState(now);

        List<MediaFile> mediaFiles = clubQueryService.media(clubId);
        List<MediaResponse> media = new ArrayList<>();
        for (MediaFile mf : mediaFiles) {
            if (mf.isMain()) continue; // 대표사진은 따로 내려줌
            media.add(new MediaResponse(mf.getId(), mf.getType(), mf.getUrl(), mf.isMain()));
        }

        return new ClubDetailResponse(
                club.getId(),
                mainImageUrl,
                club.getName(),
                club.getSummary(),
                state,
                club.getViewCount(),
                club.getCategory(),
                club.getIntroduction(),
                club.getInterviewProcess(),
                media
        );
    }
}
