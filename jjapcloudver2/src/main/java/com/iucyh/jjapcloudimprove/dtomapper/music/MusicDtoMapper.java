package com.iucyh.jjapcloudimprove.dtomapper.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import com.iucyh.jjapcloudimprove.dto.music.query.MusicSimpleDto;

public class MusicDtoMapper {

    private MusicDtoMapper() {}

    public static MusicDto toMusicDto(MusicSimpleDto musicSimpleDto) {
        return MusicDto.builder()
                .publicId(musicSimpleDto.getPublicId())
                .title(musicSimpleDto.getTitle())
                .playTime(musicSimpleDto.getPlayTime())
                .viewCount(musicSimpleDto.getViewCount())
                .build();
    }

    public static MusicDto toMusicDto(Music music) {
        return MusicDto.builder()
                .publicId(music.getPublicId())
                .title(music.getTitle())
                .playTime(music.getPlayTime())
                .viewCount(music.getViewCount())
                .build();
    }
}
