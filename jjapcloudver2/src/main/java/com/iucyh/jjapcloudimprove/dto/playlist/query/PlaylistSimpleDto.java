package com.iucyh.jjapcloudimprove.dto.playlist.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PlaylistSimpleDto {

    private String publicId;
    private String title;
    private Integer itemCount;
    private Long totalPlayTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public PlaylistSimpleDto(String publicId, String title, Integer itemCount, Long totalPlayTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.publicId = publicId;
        this.title = title;
        this.itemCount = itemCount;
        this.totalPlayTime = totalPlayTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
