package com.iucyh.jjapcloudimprove.facade.music;

import com.iucyh.jjapcloudimprove.dto.music.CreateMusicDto;
import com.iucyh.jjapcloudimprove.service.music.MusicFileService;
import com.iucyh.jjapcloudimprove.service.music.MusicFileStoreResult;
import com.iucyh.jjapcloudimprove.service.music.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicFacade {

    private final MusicService musicService;
    private final MusicFileService musicFileService;

    public String save(Long userId, CreateMusicDto musicDto, MultipartFile file) {
        MusicFileStoreResult result = null;

        try {
            result = musicFileService.storeFile(file);
            return musicService.createMusic(userId, result, musicDto);
        } catch (RuntimeException e) {
            if (result != null) {
                try {
                    musicFileService.deleteFile(result.getStoreName());
                } catch (RuntimeException e1) {
                    log.warn("Failed to rollback file: {}", result.getStoreName(), e1);
                }
            }
            throw e;
        }
    }
}
