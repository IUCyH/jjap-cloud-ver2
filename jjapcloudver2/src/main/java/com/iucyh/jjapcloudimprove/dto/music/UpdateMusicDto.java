package com.iucyh.jjapcloudimprove.dto.music;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMusicDto {

    @NotEmpty
    private String title;
}
