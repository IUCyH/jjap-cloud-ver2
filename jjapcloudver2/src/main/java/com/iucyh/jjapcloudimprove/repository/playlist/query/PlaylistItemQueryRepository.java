package com.iucyh.jjapcloudimprove.repository.playlist.query;

import com.iucyh.jjapcloudimprove.domain.music.QMusic;
import com.iucyh.jjapcloudimprove.domain.playlist.QPlaylist;
import com.iucyh.jjapcloudimprove.domain.playlist.QPlaylistItem;
import com.iucyh.jjapcloudimprove.dto.music.query.QMusicSimpleDto;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistItemSimpleDto;
import com.iucyh.jjapcloudimprove.dto.playlist.query.QPlaylistItemSimpleDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaylistItemQueryRepository {

    private final JPAQueryFactory query;

    public PlaylistItemQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<PlaylistItemSimpleDto> findPlaylistItems(Long playlistId, PlaylistItemSortType sortType, String cursor, Long cursorId, long limit) {
        QPlaylist playlist = QPlaylist.playlist;
        QPlaylistItem playlistItem = QPlaylistItem.playlistItem;
        QMusic music = QMusic.music;

        Object parsedCursor = sortType.parseCursor(cursor);
        BooleanExpression condition = switch (sortType) {
            case MUSIC_TITLE -> playlistItem.music.title.gt((String) parsedCursor).or(
                    playlistItem.music.title.eq((String) parsedCursor).and(playlistItem.id.gt(cursorId))
            );
            case POSITION -> playlistItem.position.gt((Long) parsedCursor);
        };
        OrderSpecifier<?> orderCondition = switch (sortType) {
            case MUSIC_TITLE -> playlistItem.music.title.asc();
            case POSITION -> playlistItem.position.asc();
        };

        return query
                .select(new QPlaylistItemSimpleDto(
                        playlistItem.id,
                        playlistItem.position,
                        new QMusicSimpleDto(
                                music.publicId,
                                music.title,
                                music.playTime,
                                music.viewCount,
                                music.createdAt,
                                music.updatedAt
                        )
                ))
                .from(playlistItem)
                .join(playlistItem.music, music).on(music.deletedAt.isNull())
                .join(playlistItem.playlist, playlist)
                .where(playlist.id.eq(playlistId), condition)
                .orderBy(orderCondition, playlistItem.id.asc())
                .limit(limit)
                .fetch();
    }
}
