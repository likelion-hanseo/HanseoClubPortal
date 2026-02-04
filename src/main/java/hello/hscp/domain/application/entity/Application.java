// src/main/java/hello/hscp/domain/application/entity/Application.java
package hello.hscp.domain.application.entity;

import hello.hscp.domain.club.entity.Club;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_app_club", columnList = "club_id")
})
public class Application {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(name = "student_no", nullable = false, length = 30)
    private String studentNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private int grade;

    @Lob
    @Column(name = "motivation", nullable = false)
    private String motivation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Application() {}

    public Application(Club club, String studentNo, String name, String department, int age, int grade, String motivation) {
        this.club = club;
        this.studentNo = studentNo;
        this.name = name;
        this.department = department;
        this.age = age;
        this.grade = grade;
        this.motivation = motivation;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
