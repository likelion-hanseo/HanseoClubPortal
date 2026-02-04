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

import java.util.List;

@Service
public class MediaCommandService {

    private final FileStorageClient fileStorageClient;
    private final MediaFileRepository mediaFileRepository;

    public MediaCommandService(FileStorageClient fileStorageClient, MediaFileRepository mediaFileRepository) {
        this.fileStorageClient = fileStorageClient;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public void replaceAll(Club club, MultipartFile mainImage, List<MultipartFile> mediaFiles) {
        mediaFileRepository.deleteByClub_Id(club.getId());

        saveMainImage(club, mainImage);

        if (mediaFiles != null) {
            for (MultipartFile f : mediaFiles) {
                saveExtraMedia(club, f);
            }
        }
    }

    @Transactional
    public void replaceExtrasKeepMain(Club club, List<MultipartFile> mediaFiles) {
        mediaFileRepository.deleteByClub_IdAndIsMainFalse(club.getId());
        if (mediaFiles != null) {
            for (MultipartFile f : mediaFiles) {
                saveExtraMedia(club, f);
            }
        }
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
                club, MediaType.IMAGE, stored.url(), stored.mimeType(), stored.sizeBytes(), true
        ));
    }

    @Transactional
    public void saveExtraMedia(Club club, MultipartFile file) {
        if (file == null || file.isEmpty()) return;

        String ct = file.getContentType();
        if (ct == null) throw new ApiException(ErrorCode.INVALID_FILE, "Missing content-type");

        MediaType type;
        if (ct.startsWith("image/")) type = MediaType.IMAGE;
        else if (ct.startsWith("video/")) type = MediaType.VIDEO;
        else throw new ApiException(ErrorCode.INVALID_FILE, "Media must be image/* or video/*");

        var stored = fileStorageClient.store(file);
        mediaFileRepository.save(new MediaFile(
                club, type, stored.url(), stored.mimeType(), stored.sizeBytes(), false
        ));
    }
}
