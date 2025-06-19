package com.iucyh.jjapcloudimprove.repository.playlist.query;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistSimpleDto;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistItemRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PlaylistQueryRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistItemRepository playlistItemRepository;
    @Autowired
    private PlaylistQueryRepository playlistQueryRepository;

    @Test
    @DisplayName("플레이 리스트 조회 - 성공 (플리 2개)")
    void findPlaylists() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        Playlist playlist2 = Playlist.of("playlist2");
        playlistRepository.save(playlist);
        playlistRepository.save(playlist2);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        Music music2 = Music.of("music2", "storeName", 10L, 1000L);
        Music music3 = Music.of("music3", "storeName", 10L, 1000L);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);

        // 플레이 리스트 1
        PlaylistItem playlistItem = PlaylistItem.of(100, playlist, music1);
        PlaylistItem playlistItem2 = PlaylistItem.of(100, playlist, music2);
        PlaylistItem playlistItem3 = PlaylistItem.of(100, playlist, music3);
        // 플레이 리스트 2
        PlaylistItem playlistItem4 = PlaylistItem.of(100, playlist2, music1);
        PlaylistItem playlistItem5 = PlaylistItem.of(100, playlist2, music3);

        playlistItemRepository.save(playlistItem);
        playlistItemRepository.save(playlistItem2);
        playlistItemRepository.save(playlistItem3);
        playlistItemRepository.save(playlistItem4);
        playlistItemRepository.save(playlistItem5);

        em.flush();
        em.clear();

        // when
        List<PlaylistSimpleDto> allPlaylists = playlistQueryRepository.findPlaylists(PlaylistSortType.TITLE, "", 0L, 10);
        List<PlaylistSimpleDto> playlistsWithoutPlaylist1 = playlistQueryRepository.findPlaylists(PlaylistSortType.TITLE, playlist.getTitle(), playlist.getId(), 10);
        List<PlaylistSimpleDto> playlistsByCreatedAt = playlistQueryRepository.findPlaylists(PlaylistSortType.CREATED_AT, LocalDateTime.of(9999, 12, 31, 11, 59, 59).toString(), 0L, 10);

        // 차례로 전체 플리 조회, 플리1 제목 이후 플리들 조회, 전체 플리 조회 하지만 날짜 기준으로 조회

        // then
        assertThat(allPlaylists).hasSize(2);
        assertThat(playlistsWithoutPlaylist1).hasSize(1);
        assertThat(playlistsByCreatedAt).hasSize(2);

        assertThat(allPlaylists.stream().map(p -> p.getTitle()))
                .containsExactly(
                        playlist.getTitle(),
                        playlist2.getTitle()
                );
        assertThat(allPlaylists.stream().map(p -> p.getTotalPlayTime()))
                .containsExactly(
                        30L,
                        20L
                );
        assertThat(playlistsWithoutPlaylist1.stream().map(p -> p.getTitle()))
                .containsExactly(
                        playlist2.getTitle()
                );
        assertThat(playlistsByCreatedAt.stream().map(p -> p.getTitle()))
                .containsExactly(
                        playlist2.getTitle(),
                        playlist.getTitle()
                );
    }

    @Test
    @DisplayName("플레이 리스트 조회 - 성공 (플리 1개, 총 음악 3개, 삭제된 음악 1개)")
    void findPlaylistsWithDeletedMusic() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        playlistRepository.save(playlist);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        Music music2 = Music.of("music2", "storeName", 20L, 1000L);
        Music music3 = Music.of("music3", "storeName", 30L, 1000L);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);

        // 플레이 리스트 1
        PlaylistItem playlistItem = PlaylistItem.of(100, playlist, music1);
        PlaylistItem playlistItem2 = PlaylistItem.of(100, playlist, music2);
        PlaylistItem playlistItem3 = PlaylistItem.of(100, playlist, music3);

        playlistItemRepository.save(playlistItem);
        playlistItemRepository.save(playlistItem2);
        playlistItemRepository.save(playlistItem3);

        music1.softDelete();

        em.flush();
        em.clear();

        // when
        List<PlaylistSimpleDto> allPlaylists = playlistQueryRepository.findPlaylists(PlaylistSortType.TITLE, "", 0L, 10);

        // then
        assertThat(allPlaylists).hasSize(1);
        assertThat(allPlaylists.stream().map(p -> p.getTotalPlayTime()))
                .containsExactly(
                        50L // music2 (20) + music3 (30)
                );
    }

    @Test
    @DisplayName("플레이 리스트 조회 - 성공 (비어있는 플리 1개)")
    void findPlaylistsWithEmptyItems() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        playlistRepository.save(playlist);

        em.flush();
        em.clear();

        // when
        List<PlaylistSimpleDto> allPlaylists = playlistQueryRepository.findPlaylists(PlaylistSortType.TITLE, "", 0L, 10);

        // then
        assertThat(allPlaylists).hasSize(1);
    }

    @Test
    @DisplayName("플레이 리스트 조회 - 실패 (총 플리 1개, 삭제된 플리 1개)")
    void findPlaylistsWithDeletedPlaylist() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        playlistRepository.save(playlist);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        Music music2 = Music.of("music2", "storeName", 20L, 1000L);
        Music music3 = Music.of("music3", "storeName", 30L, 1000L);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);

        // 플레이 리스트 1
        PlaylistItem playlistItem = PlaylistItem.of(100, playlist, music1);
        PlaylistItem playlistItem2 = PlaylistItem.of(100, playlist, music2);
        PlaylistItem playlistItem3 = PlaylistItem.of(100, playlist, music3);

        playlistItemRepository.save(playlistItem);
        playlistItemRepository.save(playlistItem2);
        playlistItemRepository.save(playlistItem3);

        playlist.softDelete();

        em.flush();
        em.clear();

        // when
        List<PlaylistSimpleDto> allPlaylists = playlistQueryRepository.findPlaylists(PlaylistSortType.TITLE, "", 0L, 10);

        // then
        assertThat(allPlaylists).isEmpty();
    }
}