// src/main/java/hello/hscp/domain/application/service/ApplicationAdminService.java
package hello.hscp.domain.application.service;

import hello.hscp.domain.application.entity.Application;
import hello.hscp.domain.application.repository.ApplicationRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationAdminService {

    private final ApplicationRepository applicationRepository;

    @Value("${app.application.target-club-id}")
    private Long targetClubId;

    public ApplicationAdminService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<Application> list() {
        return applicationRepository.findByClub_IdOrderByIdDesc(targetClubId);
    }

    @Transactional(readOnly = true)
    public Application getDetail(Long applicationId) {
        return applicationRepository.findByIdAndClub_Id(applicationId, targetClubId)
                .orElseThrow(() -> new ApiException(ErrorCode.APPLICATION_NOT_FOUND));
    }
}
