package com.iucyh.jjapcloudimprove.repository.playlist;

import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {

    @Query("select max(pi.position) from PlaylistItem pi where pi.playlist.id = :playlistId")
    Optional<Integer> findMaxPositionByPlaylistId(Long playlistId);
}
