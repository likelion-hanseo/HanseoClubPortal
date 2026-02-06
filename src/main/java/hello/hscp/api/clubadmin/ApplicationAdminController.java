// src/main/java/hello/hscp/api/clubadmin/ApplicationAdminController.java
package hello.hscp.api.clubadmin;

import hello.hscp.api.clubadmin.response.ApplicantDetailResponse;
import hello.hscp.api.clubadmin.response.ApplicantListItemResponse;
import hello.hscp.domain.application.entity.Application;
import hello.hscp.domain.application.service.ApplicationAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubadmin/applications")
public class ApplicationAdminController {

    private final ApplicationAdminService applicationAdminService;

    public ApplicationAdminController(ApplicationAdminService applicationAdminService) {
        this.applicationAdminService = applicationAdminService;
    }

    // 목록: 이름 + 학번만 (clubId 없음)
    @GetMapping
    public List<ApplicantListItemResponse> list() {
        return applicationAdminService.list()
                .stream()
                .map(a -> new ApplicantListItemResponse(a.getId(), a.getName(), a.getStudentNo()))
                .toList();
    }

    // 상세: 지원 내용 전체 (clubId 없음)
    @GetMapping("/{applicationId}")
    public ApplicantDetailResponse detail(@PathVariable Long applicationId) {
        Application a = applicationAdminService.getDetail(applicationId);
        return new ApplicantDetailResponse(
                a.getId(),
                a.getStudentNo(),
                a.getName(),
                a.getDepartment(),
                a.getContact(),
                a.getApplyPart(),
                a.getTechStack(),
                a.getMotivation(),
                a.getCreatedAt()
        );
    }
}
