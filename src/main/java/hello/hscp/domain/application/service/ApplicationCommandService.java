// src/main/java/hello/hscp/domain/application/service/ApplicationCommandService.java
package hello.hscp.domain.application.service;

import hello.hscp.domain.application.entity.Application;
import hello.hscp.domain.application.repository.ApplicationRepository;
import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.club.repository.ClubRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationCommandService {

    private final ClubRepository clubRepository;
    private final ApplicationRepository applicationRepository;

    @Value("${app.application.target-club-id}")
    private Long targetClubId;

    public ApplicationCommandService(ClubRepository clubRepository, ApplicationRepository applicationRepository) {
        this.clubRepository = clubRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public Long submit(String studentNo, String name, String department, int age, int grade, String motivation) {
        Club club = clubRepository.findById(targetClubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        Application app = new Application(club, studentNo, name, department, age, grade, motivation);
        applicationRepository.save(app);
        return app.getId();
    }
}
