// src/main/java/hello/hscp/api/clubadmin/ClubAdminController.java
package hello.hscp.api.clubadmin;

import hello.hscp.api.clubadmin.request.ClubUpsertRequest;
import hello.hscp.api.clubadmin.response.UpsertClubResponse;
import hello.hscp.domain.club.service.ClubCommandService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clubadmin/clubs")
public class ClubAdminController {

    private final ClubCommandService clubCommandService;

    public ClubAdminController(ClubCommandService clubCommandService) {
        this.clubCommandService = clubCommandService;
    }

    // 생성: 대표사진 필수, 추가 미디어(사진/영상) 옵션
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpsertClubResponse create(
            @RequestPart("data") @Valid ClubUpsertRequest data,
            @RequestPart("mainImage") MultipartFile mainImage,
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles
    ) {
        Long clubId = clubCommandService.create(
                data.name(),
                data.summary(),
                data.category(),
                data.recruitStartAt(),
                data.recruitEndAt(),
                data.introduction(),
                data.interviewProcess(),
                mainImage,
                mediaFiles
        );
        return new UpsertClubResponse(clubId);
    }

    // 수정: 대표사진 optional, mediaFiles optional
    @PutMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpsertClubResponse update(
            @PathVariable Long clubId,
            @RequestPart("data") @Valid ClubUpsertRequest data,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles
    ) {
        clubCommandService.update(
                clubId,
                data.name(),
                data.summary(),
                data.category(),
                data.recruitStartAt(),
                data.recruitEndAt(),
                data.introduction(),
                data.interviewProcess(),
                mainImage,
                mediaFiles
        );
        return new UpsertClubResponse(clubId);
    }

    @DeleteMapping("/{clubId}")
    public void delete(@PathVariable Long clubId) {
        clubCommandService.delete(clubId);
    }
}
