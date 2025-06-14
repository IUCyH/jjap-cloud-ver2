package com.iucyh.jjapcloudimprove.repository.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {

    Music findByPublicId(String publicId);
}
