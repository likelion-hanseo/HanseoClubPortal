// src/main/java/hello/hscp/domain/club/entity/Club.java
package hello.hscp.domain.club.entity;

import jakarta.persistence.*;
import lombok.Getter;

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

    // 모집 기간
    @Column(name = "recruit_start_at", nullable = false)
    private LocalDateTime recruitStartAt;

    @Column(name = "recruit_end_at", nullable = false)
    private LocalDateTime recruitEndAt;

    // 본문
    @Lob
    @Column(nullable = false)
    private String introduction; // 특수문자/이모티콘 가능

    @Lob
    @Column(name = "interview_process", nullable = false)
    private String interviewProcess; // 특수문자/이모티콘 가능

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
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
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

    public RecruitState recruitState(LocalDateTime now) {
        if (now.isBefore(recruitStartAt)) return RecruitState.PRE;
        if (now.isBefore(recruitEndAt)) return RecruitState.OPEN;
        return RecruitState.CLOSED;
    }

    public void update(
            String name,
            String summary,
            ClubCategory category,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
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
