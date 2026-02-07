// src/main/java/hello/hscp/domain/application/service/ApplicationAdminService.java
package hello.hscp.domain.application.service;

import hello.hscp.domain.application.entity.Application;
import hello.hscp.domain.application.repository.ApplicationRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationAdminService {

    private final ApplicationRepository applicationRepository;

    public ApplicationAdminService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<Application> list() {
        return applicationRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Application getDetail(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException(ErrorCode.APPLICATION_NOT_FOUND));
    }
}
