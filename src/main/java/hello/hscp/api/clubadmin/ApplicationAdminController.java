// src/main/java/hello/hscp/api/clubadmin/ApplicationAdminController.java
package hello.hscp.api.clubadmin;

import hello.hscp.api.clubadmin.response.ApplicantDetailResponse;
import hello.hscp.api.clubadmin.response.ApplicantListItemResponse;
import hello.hscp.api.clubadmin.response.ApplicantSummaryResponse;
import hello.hscp.domain.application.entity.Application;
import hello.hscp.domain.application.service.ApplicationAdminService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/clubadmin/applications")
public class ApplicationAdminController {

    private final ApplicationAdminService applicationAdminService;

    public ApplicationAdminController(ApplicationAdminService applicationAdminService) {
        this.applicationAdminService = applicationAdminService;
    }

    // 기존 목록: 이름 + 학번만 (유지)
    @GetMapping
    public List<ApplicantListItemResponse> list() {
        return applicationAdminService.list()
                .stream()
                .map(a -> new ApplicantListItemResponse(a.getId(), a.getName(), a.getStudentNo()))
                .toList();
    }

    // ✅ 추가 목록: 이름 + 학과 + 지원날짜(날짜만) + techStack 단어 리스트
    @GetMapping("/summary")
    public List<ApplicantSummaryResponse> listSummary() {
        return applicationAdminService.list()
                .stream()
                .map(a -> new ApplicantSummaryResponse(
                        a.getId(),
                        a.getName(),
                        a.getDepartment(),
                        toAppliedDate(a),
                        parseTechStackTags(a.getTechStack())
                ))
                .toList();
    }

    // 기존 상세: 지원 내용 전체 (유지)
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

    private static LocalDate toAppliedDate(Application a) {
        return a.getCreatedAt().toLocalDate(); // 시간 제외
    }

    /**
     * techStack 예시:
     * "Java, Spring, MySQL" / "Java Spring MySQL" / "#Java #Spring"
     * -> ["Java","Spring","MySQL"] (중복 제거, 순서 유지)
     */
    private static List<String> parseTechStackTags(String techStack) {
        if (techStack == null || techStack.isBlank()) return List.of();

        String normalized = techStack
                .replace('#', ' ')
                .replace(';', ',')
                .replace('|', ' ')
                .replace('/', ' ');

        String[] parts = normalized.split("[,\\s]+");

        Set<String> set = new LinkedHashSet<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) set.add(t);
        }
        return List.copyOf(set);
    }
}
