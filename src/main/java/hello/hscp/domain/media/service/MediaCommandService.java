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
        saveExtraImages(club, mediaFiles);
    }

    @Transactional
    public void replaceExtrasKeepMain(Club club, List<MultipartFile> mediaFiles) {
        Long clubId = club.getId();
        Long keepId = mediaFileRepository.findTop1ByClub_IdOrderByIdAsc(clubId)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_FILE, "Main image not found"))
                .getId();

        // main(최초 1개)만 남기고 나머지 삭제
        mediaFileRepository.deleteByClub_IdAndIdNot(clubId, keepId);

        // 새 extras 저장 (이미지만)
        saveExtraImages(club, mediaFiles);
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

    private void saveExtraImages(Club club, List<MultipartFile> mediaFiles) {
        if (mediaFiles == null || mediaFiles.isEmpty()) return;

        for (MultipartFile f : mediaFiles) {
            if (f == null || f.isEmpty()) continue;

            String ct = f.getContentType();
            if (ct == null || !ct.startsWith("image/")) {
                throw new ApiException(ErrorCode.INVALID_FILE, "mediaFiles must be image/*");
            }

            var stored = fileStorageClient.store(f);
            mediaFileRepository.save(new MediaFile(
                    club, MediaType.IMAGE, stored.url(), stored.mimeType(), stored.sizeBytes()
            ));
        }
    }
}
