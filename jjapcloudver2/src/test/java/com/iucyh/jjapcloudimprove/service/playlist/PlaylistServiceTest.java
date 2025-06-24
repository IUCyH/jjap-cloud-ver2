package com.iucyh.jjapcloudimprove.service.playlist;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.dto.playlist.AddPlaylistItemDto;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PlaylistServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistItemRepository playlistItemRepository;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private PlaylistService playlistService;

    @Test
    @DisplayName("플레이 리스트 음악 추가 - 성공")
    void addPlaylistItem() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        Music music = Music.of("music1", "storeName", 10L, 1000L);

        playlistRepository.save(playlist);
        musicRepository.save(music);

        em.flush();
        em.clear();

        // when
        AddPlaylistItemDto dto = new AddPlaylistItemDto();
        dto.setMusicPublicId(music.getPublicId());
        playlistService.addPlaylistItem(1L, playlist.getPublicId(), dto);

        // then
        Optional<Playlist> foundPlaylist = playlistRepository.findByPublicId(playlist.getPublicId());
        Optional<PlaylistItem> playlistItem = playlistItemRepository.findPlaylistItem(playlist.getPublicId(), music.getPublicId());

        assertThat(playlistItem).isPresent();
        assertThat(foundPlaylist).isPresent();
        assertThat(foundPlaylist.get().getItemCount()).isEqualTo(1);
        assertThat(playlistItem.get().getPosition()).isEqualTo(100);
    }

    @Test
    @DisplayName("플레이 리스트 음악 추가 - 실패 (이미 존재)")
    void addPlaylistItemFail() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        Music music = Music.of("music1", "storeName", 10L, 1000L);

        playlistRepository.save(playlist);
        musicRepository.save(music);

        em.flush();
        em.clear();

        // when
        AddPlaylistItemDto dto = new AddPlaylistItemDto();
        dto.setMusicPublicId(music.getPublicId());
        playlistService.addPlaylistItem(1L, playlist.getPublicId(), dto);

        // then
        assertThatThrownBy(() -> playlistService.addPlaylistItem(1L, playlist.getPublicId(), dto))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("플레이 리스트 음악 삭제 - 실패 (존재하는 item 없음)")
    void removePlaylistItem() {
        // given
        Playlist playlist = Playlist.of("playlist1");
        Music music = Music.of("music1", "storeName", 10L, 1000L);

        playlistRepository.save(playlist);
        musicRepository.save(music);

        em.flush();
        em.clear();

        // then
        assertThatThrownBy(
                () -> playlistService.removePlaylistItem(1L, playlist.getPublicId(), music.getPublicId())
        ).isInstanceOf(ServiceException.class);
    }
}