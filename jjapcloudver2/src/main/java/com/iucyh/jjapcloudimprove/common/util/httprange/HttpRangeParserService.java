package com.iucyh.jjapcloudimprove.common.util.httprange;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HttpRangeParserService {

    public HttpRangeResult parse(String range, long fileSize) {
        if (range == null || !range.startsWith("bytes=")) {
            return new HttpRangeResult(0, fileSize - 1);
        }

        List<HttpRange> ranges = null;
        try {
            ranges = HttpRange.parseRanges(range);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ServiceErrorCode.INVALID_RANGE_FORMAT);
        }

        if (ranges.isEmpty()) {
            return new HttpRangeResult(0, fileSize - 1);
        }

        HttpRange firstRange = ranges.get(0);
        long start = firstRange.getRangeStart(fileSize);
        long end = firstRange.getRangeEnd(fileSize);
        return new HttpRangeResult(start, end);
    }
}
