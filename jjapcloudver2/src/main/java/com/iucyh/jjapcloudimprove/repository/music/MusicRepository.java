package com.iucyh.jjapcloudimprove.repository.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, Long> {

    Optional<Music> findByPublicId(String publicId);
}
