package com.iucyh.jjapcloudimprove.service.music;

import lombok.Getter;

@Getter
public class MusicFileStoreResult {

    private String storeName;
    private long playTime;

    public MusicFileStoreResult(String storeName, long playTime) {
        this.storeName = storeName;
        this.playTime = playTime;
    }
}
