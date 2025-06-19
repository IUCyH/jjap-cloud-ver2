package com.iucyh.jjapcloudimprove.repository.playlist.query;

import com.iucyh.jjapcloudimprove.domain.music.QMusic;
import com.iucyh.jjapcloudimprove.domain.playlist.QPlaylist;
import com.iucyh.jjapcloudimprove.domain.playlist.QPlaylistItem;
import com.iucyh.jjapcloudimprove.dto.playlist.query.PlaylistSimpleDto;
import com.iucyh.jjapcloudimprove.dto.playlist.query.QPlaylistSimpleDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PlaylistQueryRepository {

    private final JPAQueryFactory query;

    public PlaylistQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<PlaylistSimpleDto> findPlaylists(PlaylistSortType sortType, String cursor, Long cursorId, long limit) {
        QPlaylist playlist = QPlaylist.playlist;
        QPlaylistItem playlistItem = QPlaylistItem.playlistItem;
        QMusic music = QMusic.music;

        Object parsedCursor = sortType.parseCursor(cursor);
        BooleanExpression condition = switch (sortType) {
            case TITLE -> playlist.title.gt((String) parsedCursor).or(
                    playlist.title.eq((String) parsedCursor).and(playlist.id.gt(cursorId))
            );
            case CREATED_AT -> playlist.createdAt.lt((LocalDateTime) parsedCursor);
            case LAST_PLAYED_DATE -> playlist.lastPlayedAt.lt((LocalDateTime) parsedCursor);
        };
        OrderSpecifier<?> orderCondition = switch (sortType) {
            case TITLE -> playlist.title.asc();
            case CREATED_AT -> playlist.createdAt.desc();
            case LAST_PLAYED_DATE -> playlist.lastPlayedAt.desc();
        };

        return query
                .select(new QPlaylistSimpleDto(
                        playlist.publicId,
                        playlist.title,
                        playlist.itemCount,
                        music.playTime.sum().coalesce(0L),
                        playlist.createdAt,
                        playlist.updatedAt
                ))
                .from(playlist)
                .leftJoin(playlist.playlistItems, playlistItem)
                .leftJoin(playlistItem.music, music).on(music.deletedAt.isNull())
                .where(
                        playlist.deletedAt.isNull(),
                        condition
                )
                .groupBy(playlist.id)
                .orderBy(orderCondition, playlist.id.asc())
                .limit(limit)
                .fetch();
    }
}
