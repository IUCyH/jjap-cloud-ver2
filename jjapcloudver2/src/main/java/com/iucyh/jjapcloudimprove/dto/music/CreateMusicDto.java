package com.iucyh.jjapcloudimprove.dto.music;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateMusicDto {

    @NotNull
    private MultipartFile file;

    @NotEmpty
    private String title;
}
