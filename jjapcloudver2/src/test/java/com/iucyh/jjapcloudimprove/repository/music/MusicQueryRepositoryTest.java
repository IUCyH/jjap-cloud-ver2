package com.iucyh.jjapcloudimprove.repository.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.dto.music.query.MusicSimpleDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MusicQueryRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private MusicQueryRepository musicQueryRepository;

    @Test
    @DisplayName("음악 조회 페이징 - 성공")
    void musicPaging() {
        // given
        Music music1 = Music.of("music1", "storeName", 10);
        Music music2 = Music.of("music2", "storeName", 10);
        Music music3 = Music.of("music3", "storeName", 10);
        Music music4 = Music.of("music4", "storeName", 10);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);
        musicRepository.save(music4);

        em.flush();
        em.clear();

        // when
        LocalDateTime now = LocalDateTime.now();
        List<MusicSimpleDto> allMusics = musicQueryRepository.findMusics(now, 4);
        List<MusicSimpleDto> musicsAfterMusic2 = musicQueryRepository.findMusics(music2.getCreatedAt(), 4);
        List<MusicSimpleDto> musicsWithLimit2 = musicQueryRepository.findMusics(now, 2);

        // then
        assertThat(allMusics).hasSize(4);
        assertThat(musicsAfterMusic2).hasSize(1);
        assertThat(musicsWithLimit2).hasSize(2);

        Stream<String> allMusicNames = allMusics.stream().map(MusicSimpleDto::getTitle);
        assertThat(allMusicNames)
                .containsExactly(
                        music4.getTitle(),
                        music3.getTitle(),
                        music2.getTitle(),
                        music1.getTitle()
                );

        Stream<String> musicAfterMusic2Names = musicsAfterMusic2.stream().map(MusicSimpleDto::getTitle);
        assertThat(musicAfterMusic2Names)
                .containsExactly(
                        music1.getTitle()
                );

        Stream<String> musicsWIthLimit2Names = musicsWithLimit2.stream().map(MusicSimpleDto::getTitle);
        assertThat(musicsWIthLimit2Names)
                .containsExactly(
                        music4.getTitle(),
                        music3.getTitle()
                );
    }

    @Test
    @DisplayName("음악 조회 페이징 - 삭제 후 결과없음")
    void musicPagingWithEmpty() {
        // given
        Music music1 = Music.of("music1", "storeName", 10);
        musicRepository.save(music1);

        // when
        music1.softDelete();
        em.flush();
        em.clear();

        LocalDateTime now = LocalDateTime.now();
        List<MusicSimpleDto> musics = musicQueryRepository.findMusics(now, 100);

        // then
        assertThat(musics).isEmpty();
    }
}