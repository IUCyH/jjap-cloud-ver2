package com.iucyh.jjapcloudimprove.common.util.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final String ROOT_DIR_NAME = "jjapcloud_file";

    @Override
    public LimitedInputStream stream(String storeName, long start, long end) {
        Path fullPath = getFullPath(storeName);

        if (!Files.exists(fullPath)) {
            throw new RuntimeException("No such file: " + storeName);
        }

        if (Files.isDirectory(fullPath)) {
            throw new RuntimeException("Target file is a directory: " + storeName);
        }

        try {
            InputStream inputStream = new FileInputStream(fullPath.toFile());
            skipBytes(inputStream, start);

            long length = end - start + 1;
            if (length <= 0) {
                throw new IllegalArgumentException("Invalid range: " + storeName + " start - " + start + ", end - " + end);
            }

            return new LimitedInputStream(inputStream, length);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + storeName, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String storeName = createStoreName(originalName);

        try (InputStream inputStream = file.getInputStream()) {
            Path rootPath = Paths.get(System.getProperty("user.home"), ROOT_DIR_NAME);
            Path fullPath = rootPath.resolve(storeName);

            Files.createDirectories(rootPath);
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
            return storeName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String storeName) {
        Path fullPath = getFullPath(storeName);
        if (Files.isDirectory(fullPath)) {
            throw new RuntimeException("Target file is a directory: " + storeName);
        }

        try {
            Files.delete(fullPath);
        } catch (NoSuchFileException e) {
            throw new RuntimeException("No such file: " + storeName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    private Path getFullPath(String storeName) {
        Path rootPath = Paths.get(System.getProperty("user.home"), ROOT_DIR_NAME);
        return rootPath.resolve(storeName);
    }

    private String createStoreName(String originalName) {
        String ext = extractExt(originalName);
        String uniqueName = UUID.randomUUID().toString().replace("-", "");
        return uniqueName + "." + ext;
    }

    private String extractExt(String originalName) {
        int pos = originalName.lastIndexOf(".");
        if (pos == -1) {
            throw new IllegalArgumentException("Invalid file name: " + originalName);
        }

        return originalName.substring(pos + 1);
    }

    private void skipBytes(InputStream inputStream, long start) {
        try {
            long totalSkipped = inputStream.skip(start);
            while (totalSkipped < start) {
                long skipped = inputStream.skip(start - totalSkipped);
                if (skipped == 0) {
                    throw new RuntimeException("Failed to skip bytes");
                }
                totalSkipped += skipped;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to skip bytes", e);
        }
    }
}
