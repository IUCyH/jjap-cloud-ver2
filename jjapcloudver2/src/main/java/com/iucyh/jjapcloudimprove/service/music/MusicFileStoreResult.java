package com.iucyh.jjapcloudimprove.service.music;

import lombok.Getter;

@Getter
public class MusicFileStoreResult {

    private String storeName;
    private long playTime;
    private long fileSize;

    public MusicFileStoreResult(String storeName, long playTime, long fileSize) {
        this.storeName = storeName;
        this.playTime = playTime;
        this.fileSize = fileSize;
    }
}
