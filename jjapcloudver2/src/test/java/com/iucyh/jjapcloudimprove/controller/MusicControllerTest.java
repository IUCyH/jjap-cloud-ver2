package com.iucyh.jjapcloudimprove.controller;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.util.file.FileMimeType;
import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.dto.IdDto;
import com.iucyh.jjapcloudimprove.dto.ResponseDto;
import com.iucyh.jjapcloudimprove.dto.music.CreateMusicDto;
import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import com.iucyh.jjapcloudimprove.dto.music.UpdateMusicDto;
import com.iucyh.jjapcloudimprove.facade.music.MusicFacade;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.service.music.MusicService;
import jakarta.activation.MimeType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MusicControllerTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private MusicFacade musicFacade;
    @Autowired
    private MusicController musicController;

    @Test
    @DisplayName("음악 리스트 조회 - 성공")
    void findMusics() {
        // given
        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        Music music2 = Music.of("music2", "storeName", 10L, 1000L);
        Music music3 = Music.of("music3", "storeName", 10L, 1000L);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);

        em.flush();
        em.clear();

        // when
        ResponseDto<List<MusicDto>> musics = musicController.getMusics(null);
        ResponseDto<List<MusicDto>> musicsWithMusic1 = musicController.getMusics(music2.getCreatedAt());

        // then
        assertThat(musics.getData()).hasSize(3);
        assertThat(musicsWithMusic1.getData()).hasSize(1);

        assertThat(
                musics.getData().stream().map(MusicDto::getTitle)
        ).containsExactly(
                music3.getTitle(),
                music2.getTitle(),
                music1.getTitle()
        );
        assertThat(
                musicsWithMusic1.getData().stream().map(MusicDto::getTitle)
        ).containsExactly(
                music1.getTitle()
        );
    }

    @Test
    @DisplayName("음악 스트리밍 - 성공")
    void getMusicFile() throws IOException {
        // given
        File file = new File("src/test/music/test-music.mp3");
        MultipartFile multipartFile = null;
        try (InputStream inputStream = new FileInputStream(file)) {
            multipartFile = new MockMultipartFile(
                    "music-file",
                    file.getName(),
                    "audio/mpeg",
                    inputStream
            );
        }

        CreateMusicDto dto = new CreateMusicDto();
        dto.setTitle("test-music");
        dto.setFile(multipartFile);
        IdDto savedId = musicFacade.save(1L, dto);

        em.flush();
        em.clear();

        // when
        ResponseEntity<InputStreamResource> allStreamedMusicFile = musicController.getMusicFile(savedId.getPublicId(), null);
        ResponseEntity<InputStreamResource> rangeStreamedMusicFile = musicController.getMusicFile(savedId.getPublicId(), "bytes=0-" + (file.length() - 50));

        // then
        assertThat(allStreamedMusicFile.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allStreamedMusicFile.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType(FileMimeType.MP3.getType()));
        assertThat(allStreamedMusicFile.getHeaders().getContentLength()).isEqualTo(file.length());

        assertThat(rangeStreamedMusicFile.getStatusCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
        assertThat(rangeStreamedMusicFile.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType(FileMimeType.MP3.getType()));
        assertThat(rangeStreamedMusicFile.getHeaders().getContentLength()).isEqualTo(file.length() - 50 + 1);
    }

    @Test
    @DisplayName("음악 스트리밍 - 실패 (invalid bytes format)")
    void getMusicFileFail() throws IOException {
        // given
        CreateMusicDto dto = createMusicDto();
        IdDto savedId = musicFacade.save(1L, dto);

        em.flush();
        em.clear();

        // then
        assertThatThrownBy(() -> musicController.getMusicFile(savedId.getPublicId(), "bytes=100-0"))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("음악 생성 - 성공")
    void createMusic() throws IOException {
        // given
        CreateMusicDto dto = createMusicDto();

        // when
        ResponseDto<IdDto> result = musicController.createMusic(1L, dto);

        // then
        assertThat(result.getData().getPublicId()).isNotNull();
    }

    @Test
    @DisplayName("음악 업데이트 - 성공")
    void musicUpdate() throws IOException {
        // given
        CreateMusicDto dto = createMusicDto();
        IdDto savedId = musicFacade.save(1L, dto);

        // when
        UpdateMusicDto updateDto = new UpdateMusicDto();
        updateDto.setTitle("new-title");
        musicController.updateMusicMetaData(1L, savedId.getPublicId(), updateDto);

        // then
        Optional<Music> music = musicRepository.findByPublicId(savedId.getPublicId());

        assertThat(music).isPresent();
        assertThat(music.get().getTitle()).isEqualTo("new-title");
    }

    @Test
    @DisplayName("음악 파일 업데이트 (replace) - 성공")
    void musicFileReplace() throws IOException {
        // given
        CreateMusicDto dto = createMusicDto();
        IdDto savedId = musicFacade.save(1L, dto);

        // when
        File file = new File("src/test/music/test-music.mp3");
        MultipartFile multipartFile = null;
        try (InputStream inputStream = new FileInputStream(file)) {
            multipartFile = new MockMultipartFile(
                    "music-file",
                    "new-test-music.mp3",
                    "audio/mpeg",
                    inputStream
            );
        }
        Music musicBeforeReplace = musicRepository.findByPublicId(savedId.getPublicId()).get();
        musicController.replaceMusicFile(1L, savedId.getPublicId(), multipartFile);

        // then
        Optional<Music> music = musicRepository.findByPublicId(savedId.getPublicId());

        assertThat(music).isPresent();
        assertThat(music.get().getStoreName()).isNotEqualTo(musicBeforeReplace.getStoreName());
    }

    @Test
    @DisplayName("음악 삭제 - 성공")
    void musicDelete() throws IOException {
        // given
        CreateMusicDto dto = createMusicDto();
        IdDto savedId = musicFacade.save(1L, dto);

        // when
        musicController.deleteMusic(1L, savedId.getPublicId());

        // then
        Optional<Music> music = musicRepository.findByPublicId(savedId.getPublicId());
        assertThat(music).isPresent();
        assertThat(music.get().getDeletedAt()).isNotNull();
    }

    private CreateMusicDto createMusicDto() throws IOException {
        File file = new File("src/test/music/test-music.mp3");
        MultipartFile multipartFile = null;
        try (InputStream inputStream = new FileInputStream(file)) {
            multipartFile = new MockMultipartFile(
                    "music-file",
                    file.getName(),
                    "audio/mpeg",
                    inputStream
            );
        }

        CreateMusicDto dto = new CreateMusicDto();
        dto.setTitle("test-music");
        dto.setFile(multipartFile);
        return dto;
    }
}