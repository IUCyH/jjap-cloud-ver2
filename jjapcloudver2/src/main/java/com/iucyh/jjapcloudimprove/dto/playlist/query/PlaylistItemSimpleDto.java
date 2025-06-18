package com.iucyh.jjapcloudimprove.dto.playlist.query;

import com.iucyh.jjapcloudimprove.dto.music.query.MusicSimpleDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class PlaylistItemSimpleDto {

    private Long id;
    private Integer position;
    private MusicSimpleDto music;

    @QueryProjection
    public PlaylistItemSimpleDto(Long id, Integer position, MusicSimpleDto music) {
        this.id = id;
        this.position = position;
        this.music = music;
    }
}
