package com.iucyh.jjapcloudimprove.controller;

import com.iucyh.jjapcloudimprove.dto.IdDto;
import com.iucyh.jjapcloudimprove.dto.ResponseDto;
import com.iucyh.jjapcloudimprove.dto.playlist.*;
import com.iucyh.jjapcloudimprove.service.playlist.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public ResponseDto<List<PlaylistDto>> getPlaylists(Long userId, @ModelAttribute FindPlaylistCondition condition) {
        return ResponseDto
                .success(playlistService.findPlaylists(userId, condition));
    }

    @GetMapping("/{playlistPublicId}/musics")
    public ResponseDto<List<PlaylistItemDto>> getPlaylistItems(@PathVariable String playlistPublicId, @ModelAttribute FindPlaylistItemCondition condition) {
        return ResponseDto
                .success(playlistService.findPlaylistItems(playlistPublicId, condition));
    }

    @PostMapping
    public ResponseDto<IdDto> createPlaylist(Long userId, @RequestBody CreatePlaylistDto dto) {
        return ResponseDto
                .success(playlistService.createPlaylist(userId, dto));
    }

    @PostMapping("/{playlistPublicId}/musics")
    public ResponseDto<Void> addMusicToPlaylist(Long userId, @PathVariable String playlistPublicId, @RequestBody AddPlaylistItemDto dto) {
        playlistService.addPlaylistItem(userId, playlistPublicId, dto);
        return ResponseDto.success(null);
    }

    @PatchMapping("/{playlistPublicId}")
    public ResponseDto<Void> updatePlaylist(Long userId, @PathVariable String playlistPublicId, @RequestBody UpdatePlaylistDto dto) {
        playlistService.updatePlaylist(userId, playlistPublicId, dto);
        return ResponseDto.success(null);
    }

    @PatchMapping("/{playlistPublicId}/last-played-date")
    public ResponseDto<Void> updatePlaylistLastPlayedDate(Long userId, @PathVariable String playlistPublicId) {
        playlistService.updateLastPlayedDate(userId, playlistPublicId);
        return ResponseDto.success(null);
    }

    @DeleteMapping("/{playlistPublicId}")
    public ResponseDto<Void> deletePlaylist(Long userId, @PathVariable String playlistPublicId) {
        playlistService.deletePlaylist(userId, playlistPublicId);
        return ResponseDto.success(null);
    }

    @DeleteMapping("/{playlistPublicId}/musics/{musicPublicId}")
    public ResponseDto<Void> deletePlaylistItem(Long userId, @PathVariable String playlistPublicId, @PathVariable String musicPublicId) {
        playlistService.removePlaylistItem(userId, playlistPublicId, musicPublicId);
        return ResponseDto.success(null);
    }
}
