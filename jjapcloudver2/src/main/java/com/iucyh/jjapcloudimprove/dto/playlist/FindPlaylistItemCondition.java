package com.iucyh.jjapcloudimprove.dto.playlist;

import com.iucyh.jjapcloudimprove.repository.playlist.query.PlaylistItemSortType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPlaylistItemCondition {

    private PlaylistItemSortType sortType;
    private String cursor;
    private Long cursorId;
}
