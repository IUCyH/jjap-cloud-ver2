package com.iucyh.jjapcloudimprove.service.playlist;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.dto.IdDto;
import com.iucyh.jjapcloudimprove.dto.playlist.*;
import com.iucyh.jjapcloudimprove.dtomapper.playlist.PlaylistDtoMapper;
import com.iucyh.jjapcloudimprove.dtomapper.playlist.PlaylistItemDtoMapper;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistItemRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistItemQueryRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistItemSortType;
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

    private static final long playlistPagingLimit = 50;
    private static final long playlistItemPagingLimit = 100;
    private static final int playlistItemPositionGap = 100;
    private final MusicService musicService;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final PlaylistQueryRepository playlistQueryRepository;
    private final PlaylistItemQueryRepository playlistItemQueryRepository;

    public List<PlaylistDto> findPlaylists(Long userId, FindPlaylistCondition condition) {
        PlaylistSortType sortType = condition.getSortType();
        String cursor = condition.getCursor();
        String cursorId = condition.getCursorId();
        Long playlistId = playlistRepository.findIdByPublicId(cursorId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));

        return playlistQueryRepository.findPlaylists(sortType, cursor, playlistId, playlistPagingLimit)
                .stream()
                .map(PlaylistDtoMapper::toPlaylistDto)
                .toList();
    }

    public List<PlaylistItemDto> findPlaylistItems(String playlistPublicId, FindPlaylistItemCondition condition) {
        PlaylistItemSortType sortType = condition.getSortType();
        String cursor = condition.getCursor();
        Long cursorId = condition.getCursorId();
        Long playlistId = playlistRepository.findIdByPublicId(playlistPublicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));

        return playlistItemQueryRepository.findPlaylistItems(playlistId, sortType, cursor, cursorId, playlistItemPagingLimit)
                .stream()
                .map(PlaylistItemDtoMapper::toPlaylistItemDto)
                .toList();
    }

    @Transactional
    public void addPlaylistItem(Long userId, String playlistPublicId, AddPlaylistItemDto addPlaylistItemDto) {
        String musicPublicId = addPlaylistItemDto.getMusicPublicId();

        Playlist playlist = playlistRepository.findByPublicId(playlistPublicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));
        Music music = musicService.findMusicEntity(musicPublicId);

        Boolean isExistsInPlaylist = playlistItemRepository.isMusicExistsInPlaylist(playlistPublicId, musicPublicId);
        if (isExistsInPlaylist) {
            throw new ServiceException(ServiceErrorCode.PLAYLIST_MUSIC_EXISTS);
        }

        Integer maxPosition = playlistItemRepository.findMaxPosition(playlist.getId())
                .orElse(0);
        PlaylistItem playlistItem = playlist.addItem(music, maxPosition + playlistItemPositionGap);
        playlistItemRepository.save(playlistItem);

        playlistRepository.increaseItemCount(playlist.getId());
    }

    @Transactional
    public void removePlaylistItem(Long userId, String playlistPublicId, RemovePlaylistItemDto removePlaylistItemDto) {
        String musicPublicId = removePlaylistItemDto.getMusicPublicId();

        PlaylistItem playlistItem = playlistItemRepository.findPlaylistItem(playlistPublicId, musicPublicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_ITEM_NOT_FOUND));
        playlistItemRepository.delete(playlistItem);

        playlistRepository.decreaseItemCount(playlistPublicId);
    }

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
