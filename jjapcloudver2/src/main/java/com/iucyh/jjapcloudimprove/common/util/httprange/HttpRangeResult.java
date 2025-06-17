package com.iucyh.jjapcloudimprove.common.util.httprange;

import lombok.Getter;

@Getter
public class HttpRangeResult {

    private long start;
    private long end;

    public HttpRangeResult(long start, long end) {
        this.start = start;
        this.end = end;
    }
}
