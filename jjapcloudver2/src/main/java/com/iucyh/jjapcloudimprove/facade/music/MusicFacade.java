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

    /**
     * Save music file and save metadata in DB,
     * if failed to save metadata in DB but success to save a file, the saved file will be rollback.
     * And if rollback is failed, it will be logged as warn
     * @param userId
     * @param musicDto
     * @param file
     * @return Saved entity's public id
     */
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
