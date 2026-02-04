// src/main/java/hello/hscp/api/common/application/ApplicationPublicController.java
package hello.hscp.api.common.application;

import hello.hscp.api.common.application.request.SubmitApplicationRequest;
import hello.hscp.api.common.application.response.SubmitApplicationResponse;
import hello.hscp.domain.application.service.ApplicationCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/applications")
public class ApplicationPublicController {

    private final ApplicationCommandService applicationCommandService;

    public ApplicationPublicController(ApplicationCommandService applicationCommandService) {
        this.applicationCommandService = applicationCommandService;
    }

    // 비로그인 지원 제출 (clubId 없음)
    @PostMapping
    public SubmitApplicationResponse submit(@RequestBody @Valid SubmitApplicationRequest req) {
        Long id = applicationCommandService.submit(
                req.studentNo(),
                req.name(),
                req.department(),
                req.age(),
                req.grade(),
                req.motivation()
        );
        return new SubmitApplicationResponse(id);
    }
}
