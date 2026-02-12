// src/main/java/hello/hscp/domain/application/entity/Application.java
package hello.hscp.domain.application.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "applications")
public class Application {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_no")
    private String studentNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String contact;

    @Column(name = "apply_part")
    private String applyPart;

    @Column(name = "tech_stack")
    private String techStack;

    @Lob
    @Column(name = "motivation", nullable = false)
    private String motivation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Application() {}

    public Application(
            String studentNo,
            String name,
            String department,
            String contact,
            String applyPart,
            String techStack,
            String motivation
    ) {
        this.studentNo = studentNo;
        this.name = name;
        this.department = department;
        this.contact = contact;
        this.applyPart = applyPart;
        this.techStack = techStack;
        this.motivation = motivation;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
