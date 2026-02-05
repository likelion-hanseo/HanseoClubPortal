// src/main/java/hello/hscp/domain/media/service/MediaCommandService.java
package hello.hscp.domain.media.service;

import hello.hscp.domain.club.entity.Club;
import hello.hscp.domain.media.entity.MediaFile;
import hello.hscp.domain.media.entity.MediaType;
import hello.hscp.domain.media.repository.MediaFileRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import hello.hscp.infra.storage.FileStorageClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaCommandService {

    private final FileStorageClient fileStorageClient;
    private final MediaFileRepository mediaFileRepository;

    public MediaCommandService(FileStorageClient fileStorageClient, MediaFileRepository mediaFileRepository) {
        this.fileStorageClient = fileStorageClient;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public void replaceMainImage(Club club, MultipartFile mainImage) {
        mediaFileRepository.deleteByClub_Id(club.getId());
        saveMainImage(club, mainImage);
    }

    @Transactional
    public void saveMainImage(Club club, MultipartFile mainImage) {
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_FILE, "Main image is required");
        }
        String ct = mainImage.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new ApiException(ErrorCode.INVALID_FILE, "Main image must be image/*");
        }

        var stored = fileStorageClient.store(mainImage);
        mediaFileRepository.save(new MediaFile(
                club, MediaType.IMAGE, stored.url(), stored.mimeType(), stored.sizeBytes()
        ));
    }
}
