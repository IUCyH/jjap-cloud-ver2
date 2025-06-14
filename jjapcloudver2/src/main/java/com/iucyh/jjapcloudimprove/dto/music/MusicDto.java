package com.iucyh.jjapcloudimprove.dto.music;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicDto {

    private String publicId;
    private String title;
    private Integer playTime;
    private Integer viewCount;
}
