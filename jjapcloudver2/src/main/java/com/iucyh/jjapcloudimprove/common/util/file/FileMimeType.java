package com.iucyh.jjapcloudimprove.common.util.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileMimeType {

    MP3("audio/mpeg");

    private final String type;
}
