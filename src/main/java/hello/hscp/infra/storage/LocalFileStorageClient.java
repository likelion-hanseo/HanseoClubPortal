// src/main/java/hello/hscp/infra/storage/LocalFileStorageClient.java
package hello.hscp.infra.storage;

import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalFileStorageClient implements FileStorageClient {

    private final Path uploadDir;
    private final String publicBaseUrl;

    public LocalFileStorageClient(
            @Value("${app.storage.upload-dir:uploads}") String uploadDir,
            @Value("${app.storage.public-base-url:}") String publicBaseUrl
    ) {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl == null ? "" : publicBaseUrl.trim();
    }

    @Override
    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_FILE, "Empty file");
        }

        try {
            Files.createDirectories(uploadDir);

            String original = file.getOriginalFilename();
            String ext = "";

            if (StringUtils.hasText(original) && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
                if (ext.length() > 10) ext = "";
            }

            String filename = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target);
            }

            String relative = "/uploads/" + filename;
            String url = publicBaseUrl.isEmpty() ? relative : publicBaseUrl + relative;

            String mime = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
            return new StoredFile(url, mime, file.getSize());

        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_FILE, "FILE_SAVE_FAILED");
        }
    }
}
