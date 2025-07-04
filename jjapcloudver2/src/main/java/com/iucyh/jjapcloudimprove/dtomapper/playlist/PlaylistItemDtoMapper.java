package com.iucyh.jjapcloudimprove.dtomapper.playlist;

import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import com.iucyh.jjapcloudimprove.dto.playlist.PlaylistItemDto;
import com.iucyh.jjapcloudimprove.dtomapper.music.MusicDtoMapper;

public class PlaylistItemDtoMapper {

    private PlaylistItemDtoMapper() {}

    public static PlaylistItemDto toPlaylistItemDto(PlaylistItem playlistItem) {
        MusicDto musicDto = MusicDtoMapper.toMusicDto(playlistItem.getMusic());
        return PlaylistItemDto.builder()
                .id(playlistItem.getId())
                .position(playlistItem.getPosition())
                .music(musicDto)
                .build();
    }
}
