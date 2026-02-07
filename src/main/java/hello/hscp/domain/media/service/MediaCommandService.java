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

    /**
     * 대표사진 1장만 운용:
     * - 기존 전부 삭제
     * - mainImage 1장 저장
     * - mediaFiles(추가 이미지)는 더 이상 지원하지 않음
     */
    @Transactional
    public void replaceAll(Club club, MultipartFile mainImage, List<MultipartFile> mediaFiles) {
        if (mediaFiles != null && mediaFiles.stream().anyMatch(f -> f != null && !f.isEmpty())) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Only mainImage is supported");
        }

        mediaFileRepository.deleteByClub_Id(club.getId());
        saveMainImage(club, mainImage);
    }

    @Transactional
    public void deleteAllByClubId(Long clubId) {
        mediaFileRepository.deleteByClub_Id(clubId);
    }

    private void saveMainImage(Club club, MultipartFile mainImage) {
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
