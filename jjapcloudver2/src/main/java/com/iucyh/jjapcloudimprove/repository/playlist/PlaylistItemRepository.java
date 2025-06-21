package com.iucyh.jjapcloudimprove.repository.playlist;

import com.iucyh.jjapcloudimprove.domain.playlist.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {

    @Query("select max(pi.position) from PlaylistItem pi where pi.playlist.id = :playlistId")
    Optional<Integer> findMaxPosition(Long playlistId);

    @Query("select pi from PlaylistItem pi where pi.playlist.publicId = :playlistPublicId and pi.music.publicId = :musicPublicId")
    Optional<PlaylistItem> findPlaylistItem(@Param("playlistPublicId") String playlistPublicId, @Param("musicPublicId") String musicPublicId);

    @Query(value = """
        select exists
            (select 1
             from playlist_items pi
             join playlists p on pi.playlist_id = p.id
             join musics m on pi.music_id = m.id
             where p.public_id = :playlistPublicId and m.public_id = :musicPublicId
            )
    """, nativeQuery = true)
    Boolean isMusicExistsInPlaylist(@Param("playlistPublicId") String playlistPublicId, @Param("musicPublicId") String musicPublicId);
}
