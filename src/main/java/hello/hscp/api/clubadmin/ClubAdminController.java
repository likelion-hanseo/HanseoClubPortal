// src/main/java/hello/hscp/api/clubadmin/ClubAdminController.java
package hello.hscp.api.clubadmin;

import tools.jackson.databind.json.JsonMapper;
import hello.hscp.api.clubadmin.request.ClubUpsertRequest;
import hello.hscp.api.clubadmin.response.UpsertClubResponse;
import hello.hscp.domain.club.service.ClubCommandService;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/clubadmin/clubs")
public class ClubAdminController {

    private final ClubCommandService clubCommandService;
    private final JsonMapper objectMapper;
    private final Validator validator;

    public ClubAdminController(
            ClubCommandService clubCommandService,
            JsonMapper objectMapper,
            Validator validator
    ) {
        this.clubCommandService = clubCommandService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpsertClubResponse create(
            @RequestPart("data") String dataJson,
            @RequestPart("mainImage") MultipartFile mainImage,
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles
    ) {
        ClubUpsertRequest data = parseAndValidate(dataJson);

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

    @PutMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UpsertClubResponse update(
            @PathVariable Long clubId,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles
    ) {
        ClubUpsertRequest data = parseAndValidate(dataJson);

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

    private ClubUpsertRequest parseAndValidate(String dataJson) {
        if (dataJson == null || dataJson.isBlank()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "'data' is required");
        }

        final ClubUpsertRequest data;
        try {
            data = objectMapper.readValue(dataJson, ClubUpsertRequest.class);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid JSON in 'data'");
        }

        Set<ConstraintViolation<ClubUpsertRequest>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            ConstraintViolation<ClubUpsertRequest> v = violations.iterator().next();
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    v.getPropertyPath() + " " + v.getMessage()
            );
        }
        return data;
    }
}
