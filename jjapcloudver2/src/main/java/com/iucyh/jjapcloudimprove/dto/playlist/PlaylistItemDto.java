package com.iucyh.jjapcloudimprove.dto.playlist;

import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaylistItemDto {

    private Long id;
    private Integer position;
    private MusicDto music;
}
