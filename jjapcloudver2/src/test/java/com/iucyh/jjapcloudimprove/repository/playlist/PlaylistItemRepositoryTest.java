package com.iucyh.jjapcloudimprove.repository.playlist;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PlaylistItemRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Test
    @DisplayName("플레이 리스트에 음악 추가 - 성공")
    void addItem() {
        // given
        Playlist playlist1 = Playlist.of("playlist1");
        playlistRepository.save(playlist1);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        musicRepository.save(music1);

        em.flush();
        em.clear();

        // when
        String playlistPublicId = playlist1.getPublicId();
        String musicPublicId = music1.getPublicId();

        Playlist foundPlaylist = playlistRepository.findByPublicId(playlistPublicId).get();
        Music foundMusic = musicRepository.findByPublicId(musicPublicId).get();

        playlistRepository.increaseItemCount(foundMusic.getId());

        Integer maxPosition = playlistItemRepository.findMaxPosition(foundPlaylist.getId())
                .orElse(0);
        PlaylistItem playlistItem = PlaylistItem.of(maxPosition + 100, playlist1, music1);
        PlaylistItem savedPlaylistItem = playlistItemRepository.save(playlistItem);

        // then
        assertThat(savedPlaylistItem.getPosition()).isEqualTo(maxPosition + 100);
        assertThat(savedPlaylistItem.getPlaylist().getTitle()).isEqualTo(playlist1.getTitle());
        assertThat(savedPlaylistItem.getMusic().getTitle()).isEqualTo(music1.getTitle());
    }

    @Test
    @DisplayName("플레이 리스트 음악 존재 여부 확인 - 성공")
    void checkItemExists() {
        // given
        Playlist playlist1 = Playlist.of("playlist1");
        playlistRepository.save(playlist1);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        musicRepository.save(music1);

        em.flush();
        em.clear();

        createPlaylistItem(playlist1, music1);

        // when
        String playlistPublicId = playlist1.getPublicId();
        String musicPublicId = music1.getPublicId();
        Boolean isExists = playlistItemRepository.isMusicExistsInPlaylist(playlistPublicId, musicPublicId);

        // then
        assertThat(isExists).isTrue();
    }

    @Test
    @DisplayName("플레이 리스트 음악 삭제 - 성공")
    void removeItem() {
        // given
        Playlist playlist1 = Playlist.of("playlist1");
        playlistRepository.save(playlist1);

        Music music1 = Music.of("music1", "storeName", 10L, 1000L);
        musicRepository.save(music1);

        em.flush();
        em.clear();

        PlaylistItem playlistItem = createPlaylistItem(playlist1, music1);

        // when
        playlistRepository.decreaseItemCount(playlist1.getPublicId());
        playlistItemRepository.delete(playlistItem);

        // then
        Optional<PlaylistItem> foundPlaylistItem = playlistItemRepository.findById(playlistItem.getId());
        assertThat(foundPlaylistItem).isEmpty();
    }

    private PlaylistItem createPlaylistItem(Playlist playlist, Music music) {
        String playlistPublicId = playlist.getPublicId();
        String musicPublicId = music.getPublicId();

        Playlist foundPlaylist = playlistRepository.findByPublicId(playlistPublicId).get();
        Music foundMusic = musicRepository.findByPublicId(musicPublicId).get();

        playlistRepository.increaseItemCount(foundMusic.getId());

        Integer maxPosition = playlistItemRepository.findMaxPosition(foundPlaylist.getId())
                .orElse(0);
        PlaylistItem playlistItem = PlaylistItem.of(maxPosition + 100, playlist, music);
        return playlistItemRepository.save(playlistItem);
    }
}
