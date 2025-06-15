package com.iucyh.jjapcloudimprove.common.util.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

class LocalFileStorageServiceTest {

    private final LocalFileStorageService fileService = new LocalFileStorageService();

    @Test
    @DisplayName("파일 저장 - 성공")
    void storeFile() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file",
                "test_file.txt",
                "text/plain",
                "hello world".getBytes(StandardCharsets.UTF_8)
        );

        // when
        String storeName = fileService.store(file);

        // then
        assertThat(storeName).isNotNull();

        Path storePath = Paths.get(System.getProperty("user.home"), "jjapcloud_file", storeName);
        assertThat(Files.exists(storePath)).isTrue();

        fileService.delete(storeName);
    }

    @Test
    @DisplayName("파일 저장 - 실패 (확장자 없음)")
    void storeFileFailed() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file",
                "test_file",
                "text/plain",
                "hello world".getBytes(StandardCharsets.UTF_8)
        );

        // then
        assertThatThrownBy(() -> fileService.store(file))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("파일 스트리밍 - 성공")
    void fileStreaming() throws IOException {
        // given
        byte[] bytes = "hello world".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile(
                "file",
                "test_file.txt",
                "text/plain",
                bytes
        );
        String storeName = fileService.store(file);

        // when
        LimitedInputStream stream = fileService.stream(storeName, 0, bytes.length - 3);

        // then
        assertThat(stream).isNotNull();

        int length = stream.readAllBytes().length;
        assertThat(length).isEqualTo(bytes.length - 3 + 1);

        fileService.delete(storeName);
    }
}