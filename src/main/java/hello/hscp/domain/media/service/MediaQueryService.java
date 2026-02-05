// src/main/java/hello/hscp/domain/media/service/MediaQueryService.java
package hello.hscp.domain.media.service;

import hello.hscp.domain.media.entity.MediaFile;
import hello.hscp.domain.media.repository.MediaFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaQueryService {

    private final MediaFileRepository mediaFileRepository;

    public MediaQueryService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional(readOnly = true)
    public String mainImageUrl(Long clubId) {
        return mediaFileRepository.findTop1ByClub_IdOrderByIdAsc(clubId)
                .map(MediaFile::getUrl)
                .orElse(null);
    }
}
