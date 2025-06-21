package com.iucyh.jjapcloudimprove.dtomapper.playlist;

import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import com.iucyh.jjapcloudimprove.dto.playlist.PlaylistItemDto;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistItemSimpleDto;
import com.iucyh.jjapcloudimprove.dtomapper.music.MusicDtoMapper;

public class PlaylistItemDtoMapper {

    private PlaylistItemDtoMapper() {}

    public static PlaylistItemDto toPlaylistItemDto(PlaylistItemSimpleDto playlistItemSimpleDto) {
        MusicDto musicDto = MusicDtoMapper.toMusicDto(playlistItemSimpleDto.getMusic());
        return PlaylistItemDto.builder()
                .id(playlistItemSimpleDto.getId())
                .position(playlistItemSimpleDto.getPosition())
                .music(musicDto)
                .build();
    }
}
