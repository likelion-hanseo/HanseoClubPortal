// src/main/java/hello/hscp/domain/media/repository/MediaFileRepository.java
package hello.hscp.domain.media.repository;

import hello.hscp.domain.media.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    Optional<MediaFile> findTop1ByClub_IdOrderByIdAsc(Long clubId);

    void deleteByClub_Id(Long clubId);
}
