// src/main/java/hello/hscp/domain/media/repository/MediaFileRepository.java
package hello.hscp.domain.media.repository;

import hello.hscp.domain.media.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    List<MediaFile> findByClub_IdOrderByIsMainDescIdAsc(Long clubId);
    Optional<MediaFile> findTop1ByClub_IdAndIsMainTrueOrderByIdAsc(Long clubId);
    void deleteByClub_Id(Long clubId);
    void deleteByClub_IdAndIsMainFalse(Long clubId);
}
