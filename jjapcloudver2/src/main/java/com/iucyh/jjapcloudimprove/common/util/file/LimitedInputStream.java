package com.iucyh.jjapcloudimprove.common.util.file;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream {

    private long remaining;

    public LimitedInputStream(InputStream in, long limit) {
        super(in);
        this.remaining = limit;
    }

    @Override
    public int read() throws IOException {
        if (remaining <= 0) {
            return -1;
        }

        int result = in.read();
        if (result != -1) {
            remaining--;
        }

        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining <= 0) {
            return -1;
        }

        int result = in.read(b, off, (int) Math.min(len, remaining));
        if (result != -1) {
            remaining -= result;
        }

        return result;
    }
}
