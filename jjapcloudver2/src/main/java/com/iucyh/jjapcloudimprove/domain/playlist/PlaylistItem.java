package com.iucyh.jjapcloudimprove.domain.playlist;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "playlist_items")
@Getter
public class PlaylistItem {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id")
    private Music music;

    protected PlaylistItem() {}

    public static PlaylistItem of(Integer position, Playlist playlist, Music music) {
        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.position = position;
        playlistItem.playlist = playlist;
        playlistItem.music = music;
        return playlistItem;
    }
}
