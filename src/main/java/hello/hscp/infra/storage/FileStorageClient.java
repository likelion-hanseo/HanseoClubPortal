// src/main/java/hello/hscp/infra/storage/FileStorageClient.java
package hello.hscp.infra.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {
    StoredFile store(MultipartFile file);

    record StoredFile(String url, String mimeType, long sizeBytes) {}
}
