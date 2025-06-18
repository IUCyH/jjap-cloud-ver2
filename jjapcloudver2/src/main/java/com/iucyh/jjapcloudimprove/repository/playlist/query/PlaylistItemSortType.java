package com.iucyh.jjapcloudimprove.repository.playlist.query;

public enum PlaylistItemSortType {

    MUSIC_TITLE {
        @Override
        public Object parseCursor(String cursor) {
            return cursor;
        }
    },
    POSITION {
        @Override
        public Object parseCursor(String cursor) {
            return Long.parseLong(cursor);
        }
    };

    public abstract Object parseCursor(String cursor);
}
