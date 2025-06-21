package com.iucyh.jjapcloudimprove.dto.playlist;

import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistSortType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPlaylistCondition {

    private PlaylistSortType sortType;
    private String cursor;
    private String cursorId;
}
