package com.iucyh.jjapcloudimprove.common.util.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {

    LimitedInputStream stream(String storeName, long start, long end);
    String store(MultipartFile file);
    void delete(String storeName);
    boolean checkMimeType(MultipartFile file, FileMimeType mimeType);
}
