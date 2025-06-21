package com.iucyh.jjapcloudimprove.dto.playlist;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaylistDto {

    private String publicId;
    private String title;
    private Integer itemCount;
    private Long totalPlayTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
