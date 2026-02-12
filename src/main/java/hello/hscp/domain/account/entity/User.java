// src/main/java/hello/hscp/domain/account/entity/User.java
package hello.hscp.domain.account.entity;

import hello.hscp.global.security.Role;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_login_id", columnNames = "login_id")
})
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected User() {}

    public User(String loginId, String passwordHash, Role role) {
        this.loginId = loginId;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) this.role = Role.ADMIN;
    }
}
