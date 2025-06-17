package com.iucyh.jjapcloudimprove.repository.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.domain.music.QMusic;
import com.iucyh.jjapcloudimprove.dto.music.query.MusicSimpleDto;
import com.iucyh.jjapcloudimprove.dto.music.query.QMusicSimpleDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
public class MusicQueryRepository {

    private final JPAQueryFactory query;

    public MusicQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<MusicSimpleDto> findMusics(LocalDateTime date, long limit) {
        QMusic music = QMusic.music;

        return query
                .select(
                        new QMusicSimpleDto(
                                music.publicId,
                                music.title,
                                music.playTime,
                                music.viewCount,
                                music.createdAt,
                                music.updatedAt
                        )
                )
                .from(music)
                .where(music.createdAt.lt(date), music.deletedAt.isNull())
                .orderBy(music.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}
