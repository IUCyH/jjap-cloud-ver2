package com.iucyh.jjapcloudimprove.service.playlist;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.dto.playlist.FindPlaylistItemCondition;
import com.iucyh.jjapcloudimprove.dto.playlist.PlaylistItemDto;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistItemSimpleDto;
import com.iucyh.jjapcloudimprove.dtomapper.playlist.PlaylistItemDtoMapper;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistItemRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.PlaylistRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistItemQueryRepository;
import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistItemSortType;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistItemService {

    private static final long pagingLimit = 100;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemQueryRepository playlistItemQueryRepository;

    public List<PlaylistItemDto> findPlaylistItems(String playlistPublicId, FindPlaylistItemCondition condition) {
        PlaylistItemSortType sortType = condition.getSortType();
        String cursor = condition.getCursor();
        Long cursorId = condition.getCursorId();
        Long playlistId = playlistRepository.findIdByPublicId(playlistPublicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.PLAYLIST_NOT_FOUND));

        return playlistItemQueryRepository.findPlaylistItems(playlistId, sortType, cursor, cursorId, pagingLimit)
                .stream()
                .map(PlaylistItemDtoMapper::toPlaylistItemDto)
                .toList();
    }
}
