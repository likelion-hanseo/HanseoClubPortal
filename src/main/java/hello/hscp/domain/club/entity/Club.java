// src/main/java/hello/hscp/domain/club/entity/Club.java
package hello.hscp.domain.club.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "clubs")
public class Club {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대표 정보
    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 200)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClubCategory category;

    // 모집 기간 (날짜만 + null 허용)
    @Column(name = "recruit_start_at")
    private LocalDate recruitStartAt;

    @Column(name = "recruit_end_at")
    private LocalDate recruitEndAt;

    // 본문
    @Lob
    @Column(nullable = false)
    private String introduction;

    @Lob
    @Column(name = "interview_process", nullable = false)
    private String interviewProcess;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Club() {}

    public Club(
            String name,
            String summary,
            ClubCategory category,
            LocalDate recruitStartAt,
            LocalDate recruitEndAt,
            String introduction,
            String interviewProcess
    ) {
        this.name = name;
        this.summary = summary;
        this.category = category;
        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
        this.introduction = introduction;
        this.interviewProcess = interviewProcess;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.viewCount = 0;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    // null 허용 recruitStartAt/endAt 대응
    public RecruitState recruitState(LocalDateTime now) {
        if (recruitStartAt == null || recruitEndAt == null) return RecruitState.UNKNOWN;

        LocalDate today = now.toLocalDate();
        if (today.isBefore(recruitStartAt)) return RecruitState.PRE;
        if (!today.isAfter(recruitEndAt)) return RecruitState.OPEN; // end 포함
        return RecruitState.CLOSED;
    }

    public void update(
            String name,
            String summary,
            ClubCategory category,
            LocalDate recruitStartAt,
            LocalDate recruitEndAt,
            String introduction,
            String interviewProcess
    ) {
        this.name = name;
        this.summary = summary;
        this.category = category;
        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
        this.introduction = introduction;
        this.interviewProcess = interviewProcess;
    }
}
