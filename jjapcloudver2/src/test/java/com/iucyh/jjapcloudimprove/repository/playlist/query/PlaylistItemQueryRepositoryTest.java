package com.iucyh.jjapcloudimprove.repository.playlist.query;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistItemSimpleDto;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistItemRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PlaylistItemQueryRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistItemRepository playlistItemRepository;
    @Autowired
    private PlaylistItemQueryRepository playlistItemQueryRepository;

    @Test
    @DisplayName("플레이 리스트 아이템 조회 - 성공 (플리 1개, 아이템 3개)")
    void findPlaylistItems() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        playlistRepository.save(playlist);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        Music music2 = Music.of("music2", "storeName", 10L, 1000L);
        Music music3 = Music.of("music3", "storeName", 10L, 1000L);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);

        PlaylistItem playlistItem1 = PlaylistItem.of(100, playlist, music1);
        PlaylistItem playlistItem2 = PlaylistItem.of(200, playlist, music2);
        PlaylistItem playlistItem3 = PlaylistItem.of(300, playlist, music3);

        playlistItemRepository.save(playlistItem1);
        playlistItemRepository.save(playlistItem2);
        playlistItemRepository.save(playlistItem3);

        em.flush();
        em.clear();

        // when
        List<PlaylistItemSimpleDto> allPlaylistItems = playlistItemQueryRepository.findPlaylistItems(playlist.getId(), PlaylistItemSortType.MUSIC_TITLE, "", 0L, 10);
        List<PlaylistItemSimpleDto> playlistItemsWithoutMusic1 = playlistItemQueryRepository.findPlaylistItems(playlist.getId(), PlaylistItemSortType.MUSIC_TITLE, playlistItem1.getMusic().getTitle(), playlistItem1.getId(), 10);
        List<PlaylistItemSimpleDto> playlistItemsByPosition = playlistItemQueryRepository.findPlaylistItems(playlist.getId(), PlaylistItemSortType.POSITION, playlistItem2.getPosition().toString(), playlistItem2.getId(), 10);

        // then
        assertThat(allPlaylistItems).hasSize(3);
        assertThat(playlistItemsWithoutMusic1).hasSize(2);
        assertThat(playlistItemsByPosition).hasSize(1);

        assertThat(allPlaylistItems.stream().map(p -> p.getMusic().getTitle()))
                .containsExactly(
                        playlistItem1.getMusic().getTitle(),
                        playlistItem2.getMusic().getTitle(),
                        playlistItem3.getMusic().getTitle()
                );
        assertThat(playlistItemsWithoutMusic1.stream().map(p -> p.getMusic().getTitle()))
                .containsExactly(
                        playlistItem2.getMusic().getTitle(),
                        playlistItem3.getMusic().getTitle()
                );
        assertThat(playlistItemsByPosition.stream().map(p -> p.getMusic().getTitle()))
                .containsExactly(
                        playlistItem3.getMusic().getTitle()
                );
    }

    @Test
    @DisplayName("플레이 리스트 아이템 조회 - 성공 (플리 1개, 아이템 3개, 삭제된 음악(아이템) 1개)")
    void findPlaylistItemsWithDeletedMusic() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        playlistRepository.save(playlist);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        Music music2 = Music.of("music2", "storeName", 10L, 1000L);
        Music music3 = Music.of("music3", "storeName", 10L, 1000L);

        musicRepository.save(music1);
        musicRepository.save(music2);
        musicRepository.save(music3);

        PlaylistItem playlistItem1 = PlaylistItem.of(100, playlist, music1);
        PlaylistItem playlistItem2 = PlaylistItem.of(200, playlist, music2);
        PlaylistItem playlistItem3 = PlaylistItem.of(300, playlist, music3);

        playlistItemRepository.save(playlistItem1);
        playlistItemRepository.save(playlistItem2);
        playlistItemRepository.save(playlistItem3);

        music1.softDelete();

        em.flush();
        em.clear();

        // when
        List<PlaylistItemSimpleDto> allPlaylistItems = playlistItemQueryRepository.findPlaylistItems(playlist.getId(), PlaylistItemSortType.MUSIC_TITLE, "", 0L, 10);

        // then
        assertThat(allPlaylistItems).hasSize(2);
        assertThat(allPlaylistItems.stream().map(p -> p.getMusic().getTitle()))
                .containsExactly(
                        playlistItem2.getMusic().getTitle(),
                        playlistItem3.getMusic().getTitle()
                );
    }

    @Test
    @DisplayName("플레이 리스트 아이템 조회 - 성공 (총 플리 2개, 조회하는 기준 플리는 1개)")
    void findPlaylistItemsWithTwoPlaylists() {
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

        PlaylistItem playlistItem1 = PlaylistItem.of(100, playlist, music1);
        PlaylistItem playlistItem2 = PlaylistItem.of(200, playlist, music2);
        PlaylistItem playlistItem3 = PlaylistItem.of(300, playlist, music3);

        PlaylistItem playlistItem4 = PlaylistItem.of(100, playlist2, music1);
        PlaylistItem playlistItem5 = PlaylistItem.of(200, playlist2, music3);

        playlistItemRepository.save(playlistItem1);
        playlistItemRepository.save(playlistItem2);
        playlistItemRepository.save(playlistItem3);

        playlistItemRepository.save(playlistItem4);
        playlistItemRepository.save(playlistItem5);

        em.flush();
        em.clear();

        // when
        List<PlaylistItemSimpleDto> allPlaylistItems = playlistItemQueryRepository.findPlaylistItems(playlist.getId(), PlaylistItemSortType.MUSIC_TITLE, "", 0L, 10);

        // then
        assertThat(allPlaylistItems).hasSize(3); // 플리 1은 3개, 플리 2는 2개 -> 플리 1 기준으로 조회 하므로 3개가 나와야 함
    }

    @Test
    @DisplayName("플레이 리스트 아이템 조회 - 실패 (플리 1개, 아이템 0개)")
    void findPlaylistItemsWithEmptyItems() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        playlistRepository.save(playlist);

        em.flush();
        em.clear();

        // when
        List<PlaylistItemSimpleDto> allPlaylistItems = playlistItemQueryRepository.findPlaylistItems(playlist.getId(), PlaylistItemSortType.MUSIC_TITLE, "", 0L, 10);

        // then
        assertThat(allPlaylistItems).isEmpty();
    }
}