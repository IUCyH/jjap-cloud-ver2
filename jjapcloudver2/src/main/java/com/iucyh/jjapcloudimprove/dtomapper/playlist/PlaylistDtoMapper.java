package com.iucyh.jjapcloudimprove.dtomapper.playlist;

import com.iucyh.jjapcloudimprove.dto.playlist.PlaylistDto;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistSimpleDto;

public class PlaylistDtoMapper {

    private PlaylistDtoMapper() {}

    public static PlaylistDto toPlaylistDto(PlaylistSimpleDto playlistSimpleDto) {
        return PlaylistDto.builder()
                .publicId(playlistSimpleDto.getPublicId())
                .title(playlistSimpleDto.getTitle())
                .itemCount(playlistSimpleDto.getItemCount())
                .totalPlayTime(playlistSimpleDto.getTotalPlayTime())
                .createdAt(playlistSimpleDto.getCreatedAt())
                .updatedAt(playlistSimpleDto.getUpdatedAt())
                .build();
    }
}
