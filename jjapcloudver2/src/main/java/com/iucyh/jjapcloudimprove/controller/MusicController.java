package com.iucyh.jjapcloudimprove.controller;

import com.iucyh.jjapcloudimprove.common.util.file.FileMimeType;
import com.iucyh.jjapcloudimprove.common.util.httprange.HttpRangeParserService;
import com.iucyh.jjapcloudimprove.common.util.httprange.HttpRangeResult;
import com.iucyh.jjapcloudimprove.dto.IdDto;
import com.iucyh.jjapcloudimprove.dto.ResponseDto;
import com.iucyh.jjapcloudimprove.dto.music.CreateMusicDto;
import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import com.iucyh.jjapcloudimprove.dto.music.UpdateMusicDto;
import com.iucyh.jjapcloudimprove.facade.music.MusicFacade;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.repository.music.projection.MusicMetaDataProjection;
import com.iucyh.jjapcloudimprove.service.music.MusicFileService;
import com.iucyh.jjapcloudimprove.service.music.MusicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/musics")
@RequiredArgsConstructor
public class MusicController {

    private final HttpRangeParserService rangeParserService;
    private final MusicRepository musicRepository;
    private final MusicService musicService;
    private final MusicFileService musicFileService;
    private final MusicFacade musicFacade;

    @GetMapping
    public ResponseDto<List<MusicDto>> getMusics(@RequestParam(name = "last-item-date", required = false) LocalDateTime date) {
        if (date == null) {
            date = getMaxDate();
        }
        return ResponseDto
                .success(musicService.findMusics(date));
    }

    @GetMapping("/{musicPublicId}/file")
    public ResponseEntity<InputStreamResource> getMusicFile(
            @PathVariable String musicPublicId,
            @RequestHeader(name = "Range", required = false) String range
    ) {
        Optional<MusicMetaDataProjection> metaDataOptional = musicRepository.findMetaDataByPublicId(musicPublicId);
        if (metaDataOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        MusicMetaDataProjection metaData = metaDataOptional.get();
        HttpRangeResult parseResult = rangeParserService.parse(range, metaData.getFileSize());
        InputStreamResource resource = musicFileService.streamFile(metaData.getStoreName(), parseResult.getStart(), parseResult.getEnd());

        ResponseEntity.BodyBuilder result = ResponseEntity
                .status(range != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .header("Content-Type", FileMimeType.MP3.getType())
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", String.valueOf(parseResult.getEnd() - parseResult.getStart() + 1));
        if (range != null) {
            result.header("Content-Range", String.format("bytes %d-%d/%d", parseResult.getStart(), parseResult.getEnd(), metaData.getFileSize()));
        }
        return result.body(resource);
    }

    @PostMapping
    public ResponseDto<IdDto> createMusic(Long userId, @Valid @ModelAttribute CreateMusicDto musicDto) {
        return ResponseDto
                .success(musicFacade.save(userId, musicDto));
    }

    @PatchMapping("/{musicPublicId}")
    public ResponseDto<Void> updateMusicMetaData(
            Long userId,
            @PathVariable String musicPublicId,
            @Valid @RequestBody UpdateMusicDto musicDto
    ) {
        musicService.updateMusic(userId, musicPublicId, musicDto);
        return ResponseDto
                .success(null);
    }

    @PostMapping("/{musicPublicId}/file")
    public ResponseDto<Void> replaceMusicFile(
            Long userId,
            @PathVariable String musicPublicId,
            @RequestParam("music-file") MultipartFile musicFile
    ) {
        musicFacade.replaceFile(userId, musicPublicId, musicFile);
        return ResponseDto
                .success(null);
    }

    @DeleteMapping("/{musicPublicId}")
    public ResponseDto<Void> deleteMusic(Long userId, @PathVariable String musicPublicId) {
        musicService.deleteMusic(userId, musicPublicId);
        return ResponseDto
                .success(null);
    }

    private LocalDateTime getMaxDate() {
        return LocalDateTime.of(9999, 12, 31, 11, 59, 59);
    }
}
