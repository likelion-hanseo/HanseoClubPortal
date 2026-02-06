// src/main/java/hello/hscp/domain/application/service/ApplicationCommandService.java
package hello.hscp.domain.application.service;

import hello.hscp.domain.application.entity.Application;
import hello.hscp.domain.application.repository.ApplicationRepository;
import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.club.repository.ClubRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationCommandService {

    private static final String NOTIFY_EMAIL = "iyeojae1@gmail.com";

    private final ClubRepository clubRepository;
    private final ApplicationRepository applicationRepository;
    private final JavaMailSender mailSender;

    @Value("${app.application.target-club-id}")
    private Long targetClubId;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ApplicationCommandService(
            ClubRepository clubRepository,
            ApplicationRepository applicationRepository,
            JavaMailSender mailSender
    ) {
        this.clubRepository = clubRepository;
        this.applicationRepository = applicationRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public Long submit(
            String studentNo,
            String name,
            String department,
            String contact,
            String applyPart,
            String techStack,
            String motivation
    ) {
        Club club = clubRepository.findById(targetClubId)
                .orElseThrow(() -> new ApiException(ErrorCode.CLUB_NOT_FOUND));

        Application app = new Application(
                club,
                studentNo,
                name,
                department,
                contact,
                applyPart,
                techStack,
                motivation
        );
        applicationRepository.save(app);

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(NOTIFY_EMAIL);
            msg.setSubject("[HSCP] 지원이 접수되었습니다");
            msg.setText("""
                    - 학번 : %s
                    - 이름 : %s
                    - 학과 : %s
                    - 연락처 : %s
                    - 지원파트 : %s
                    - 기술스택 : %s

                    - 지원동기 :
                    %s
                    """.formatted(
                    studentNo,
                    name,
                    department,
                    contact,
                    applyPart,
                    techStack,
                    motivation
            ));
            mailSender.send(msg);
        } catch (Exception ignored) {
            // 메일 실패는 무시(지원 저장 롤백 방지)
        }

        return app.getId();
    }
}
