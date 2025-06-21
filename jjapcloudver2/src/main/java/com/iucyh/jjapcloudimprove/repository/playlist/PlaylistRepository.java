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
    @Query("update Playlist p set p.itemCount = p.itemCount + 1 where p.id = :playlistId")
    void increaseItemCount(Long playlistId);

    @Modifying(clearAutomatically = true)
    @Query("update Playlist p set p.itemCount = case when (p.itemCount - 1) < 0 then 0 else p.itemCount - 1 end where p.publicId = :playlistPublicId")
    void decreaseItemCount(String playlistPublicId);

    @Modifying(clearAutomatically = true)
    @Query("update Playlist p set p.lastPlayedAt = :lastPlayedAt where p.publicId = :publicId")
    void updateLastPlayedAt(@Param("lastPlayedAt") LocalDateTime lastPlayedAt, @Param("publicId") String publicId);
}
