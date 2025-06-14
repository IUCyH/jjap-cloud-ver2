package com.iucyh.jjapcloudimprove.dto.music;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateMusicDto {

    @NotEmpty
    private String title;

    private MultipartFile musicFile;
}
