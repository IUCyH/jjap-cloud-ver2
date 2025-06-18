package com.iucyh.jjapcloudimprove.repository.playlist.query;

import java.time.LocalDateTime;

public enum PlaylistSortType {

    TITLE {
        @Override
        public Object parseCursor(String cursor) {
            return cursor;
        }
    },
    CREATED_AT {
        @Override
        public Object parseCursor(String cursor) {
            return LocalDateTime.parse(cursor);
        }
    },
    LAST_PLAYED_DATE {
        @Override
        public Object parseCursor(String cursor) {
            return LocalDateTime.parse(cursor);
        }
    };

    public abstract Object parseCursor(String cursor);
}
