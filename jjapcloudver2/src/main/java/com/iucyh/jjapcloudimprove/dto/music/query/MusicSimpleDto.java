package com.iucyh.jjapcloudimprove.dto.music.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MusicSimpleDto {

    private final String publicId;
    private final String title;
    private final Integer playTime;
    private final Integer viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public MusicSimpleDto(String publicId, String title, Integer playTime, Integer viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.publicId = publicId;
        this.title = title;
        this.playTime = playTime;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
