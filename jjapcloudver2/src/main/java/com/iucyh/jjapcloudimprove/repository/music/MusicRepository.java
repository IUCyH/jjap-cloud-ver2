package com.iucyh.jjapcloudimprove.repository.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, Long> {

    Optional<Music> findByPublicId(String publicId);

    @Query("select m.storeName from Music m where m.publicId = :publicId")
    Optional<String> findStoreName(String publicId);

    @Modifying(clearAutomatically = true)
    @Query("update Music m set m.storeName = :storeName, m.fileSize = :fileSize where m.publicId = :publicId")
    void updateMetaData(String publicId, String storeName, long fileSize);
}
