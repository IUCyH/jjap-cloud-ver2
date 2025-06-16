package com.iucyh.jjapcloudimprove.dto;

import lombok.Getter;

@Getter
public class IdDto {

    private String publicId;

    public IdDto(String publicId) {
        this.publicId = publicId;
    }
}
