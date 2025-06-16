package com.iucyh.jjapcloudimprove.domain.music;

import com.iucyh.jjapcloudimprove.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "musics")
@Getter
public class Music extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 32, nullable = false, unique = true, updatable = false)
    private String publicId;

    @Column(length = 128, nullable = false)
    private String title;

    @Column(length = 32, nullable = false, updatable = false)
    private String storeName;

    @Column(nullable = false)
    private Long playTime;

    @Column(nullable = false)
    private Integer viewCount;

    protected Music() {}

    public static Music of(String title, String storeName, Long playTime) {
        Music music = new Music();
        music.title = title;
        music.storeName = storeName;
        music.playTime = playTime;
        music.viewCount = 0;
        music.publicId = UUID.randomUUID().toString().replace("-", "");
        return music;
    }

    public void update(String title) {
        if(title != null) {
            this.title = title;
        }
    }
}
