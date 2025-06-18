package com.iucyh.jjapcloudimprove.domain.playlist;

import com.iucyh.jjapcloudimprove.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "playlists")
@Getter
public class Playlist extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 32, nullable = false, unique = true, updatable = false)
    private String publicId;

    @Column(length = 64, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer itemCount;

    protected Playlist() {}

    public static Playlist of(String title) {
        Playlist playlist = new Playlist();
        playlist.title = title;
        playlist.itemCount = 0;
        playlist.publicId = BaseEntity.generatePublicId();
        return playlist;
    }
}
