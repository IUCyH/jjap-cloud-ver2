package com.iucyh.jjapcloudimprove.service.playlist;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.dto.IdDto;
import com.iucyh.jjapcloudimprove.dto.playlist.AddPlaylistItemDto;
import com.iucyh.jjapcloudimprove.dto.playlist.FindPlaylistCondition;
import com.iucyh.jjapcloudimprove.dto.playlist.PlaylistDto;
import com.iucyh.jjapcloudimprove.dtomapper.playlist.PlaylistDtoMapper;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistItemRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistQueryRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistSortType;
import com.iucyh.jjapcloudimprove.service.music.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistService {

    private static final long pagingLimit = 50;
    private static final int playlistItemPositionGap = 100;
    private final MusicService musicService;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final PlaylistQueryRepository playlistQueryRepository;

    public List<PlaylistDto> findPlaylists(Long userId, FindPlaylistCondition condition) {
        PlaylistSortType sortType = condition.getSortType();
        String cursor = condition.getCursor();
        String cursorId = condition.getCursorId();
        Long playlistId = playlistRepository.findIdByPublicId(cursorId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));

        return playlistQueryRepository.findPlaylists(sortType, cursor, playlistId, pagingLimit)
                .stream()
                .map(PlaylistDtoMapper::toPlaylistDto)
                .toList();
    }

    @Transactional
    public void addPlaylistItem(Long userId, AddPlaylistItemDto addPlaylistItemDto) {
        String playlistPublicId = addPlaylistItemDto.getPlaylistPublicId();
        String musicPublicId = addPlaylistItemDto.getMusicPublicId();

        Playlist playlist = playlistRepository.findByPublicId(playlistPublicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));
        Music music = musicService.findMusicEntity(musicPublicId);

        Boolean isExistsInPlaylist = playlistRepository.isMusicExistsInPlaylist(playlistPublicId, musicPublicId);
        if (isExistsInPlaylist) {
            throw new ServiceException(ServiceErrorCode.PLAYLIST_MUSIC_EXISTS);
        }

        Integer maxPosition = playlistItemRepository.findMaxPositionByPlaylistId(playlist.getId())
                        .orElse(0);

        PlaylistItem playlistItem = playlist.addItem(music, maxPosition + playlistItemPositionGap);
        playlistItemRepository.save(playlistItem);
    }

    // TODO: 플리 음악 삭제 구현

    @Transactional
    public IdDto createPlaylist(Long userId, String title) {
        Playlist playlist = Playlist.of(title);
        playlistRepository.save(playlist);
        return new IdDto(playlist.getPublicId());
    }

    @Transactional
    public void updatePlaylist(Long userId, String publicId, String title) {
        Playlist foundPlaylist = playlistRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));
        foundPlaylist.update(title);
    }

    @Transactional
    public void updateLastPlayedDate(Long userId, String publicId) {
        playlistRepository.updateLastPlayedAt(LocalDateTime.now(), publicId);
    }

    @Transactional
    public void deletePlaylist(Long userId, String publicId) {
        Playlist foundPlaylist = playlistRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));
        foundPlaylist.softDelete();
    }
}
