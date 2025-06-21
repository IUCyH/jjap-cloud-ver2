package com.iucyh.jjapcloudimprove.repository.playlist;

import com.iucyh.jjapcloudimprove.domain.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("select p.id from Playlist p where p.publicId = :publicId")
    Optional<Long> findIdByPublicId(String publicId);

    Optional<Playlist> findByPublicId(String publicId);

    @Modifying(clearAutomatically = true)
    @Query("update Playlist p set p.lastPlayedAt = :lastPlayedAt where p.publicId = :publicId")
    void updateLastPlayedAt(@Param("lastPlayedAt") LocalDateTime lastPlayedAt, @Param("publicId") String publicId);

    @Query(value = "select exists (select 1 from playlist_items pi join playlists p on p.id = pi.playlist_id join musics m on m.id = pi.music_id where p.public_id = :playlistPublicId and m.public_id = :musicPublicId)", nativeQuery = true)
    Boolean isMusicExistsInPlaylist(@Param("playlistPublicId") String playlistPublicId, @Param("musicPublicId") String musicPublicId);
}
