// src/main/java/hello/hscp/domain/media/entity/MediaFile.java
package hello.hscp.domain.media.entity;

import hello.hscp.domain.club.entity.Club;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "media_files", indexes = {
        @Index(name = "idx_media_club", columnList = "club_id")
})
public class MediaFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    @Column(nullable = false)
    private String url;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected MediaFile() {}

    public MediaFile(Club club, MediaType type, String url, String mimeType, long sizeBytes) {
        this.club = club;
        this.type = type;
        this.url = url;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
