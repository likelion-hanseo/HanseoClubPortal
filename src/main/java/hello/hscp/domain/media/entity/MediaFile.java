// src/main/java/hello/hscp/domain/media/entity/MediaFile.java
package hello.hscp.domain.media.entity;

import hello.hscp.domain.club.entity.Club;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "media_files", indexes = {
        @Index(name = "idx_media_club", columnList = "club_id"),
        @Index(name = "idx_media_club_main", columnList = "club_id,is_main")
})
public class MediaFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MediaType type;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "is_main", nullable = false)
    private boolean isMain;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected MediaFile() {}

    public MediaFile(Club club, MediaType type, String url, String mimeType, long sizeBytes, boolean isMain) {
        this.club = club;
        this.type = type;
        this.url = url;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.isMain = isMain;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
