package com.iucyh.jjapcloudimprove.facade.music;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.dto.IdDto;
import com.iucyh.jjapcloudimprove.dto.music.CreateMusicDto;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import com.iucyh.jjapcloudimprove.service.music.MusicFileService;
import com.iucyh.jjapcloudimprove.service.music.MusicFileStoreResult;
import com.iucyh.jjapcloudimprove.service.music.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicFacade {

    private final MusicRepository musicRepository;
    private final MusicService musicService;
    private final MusicFileService musicFileService;

    /**
     * Save music file and save metadata in DB,
     * if failed to save metadata in DB but success to save a file, the saved file will be rollback.
     * And if rollback is failed, it will be logged as warn
     * @param userId
     * @param musicDto
     * @return Saved entity's public id
     */
    public IdDto save(Long userId, CreateMusicDto musicDto) {
        MusicFileStoreResult result = null;

        try {
            result = musicFileService.storeFile(musicDto.getFile());
            String storeName = musicService.createMusic(userId, result, musicDto);
            return new IdDto(storeName);
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

    @Transactional
    public void replaceFile(Long userId, String musicPublicId, MultipartFile musicFile) {
        String oldStoreName = musicRepository.findStoreName(musicPublicId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.MUSIC_NOT_FOUND));
        String newStoreName = null;

        try {
            newStoreName = musicFileService.replaceFile(musicFile, oldStoreName);
            musicRepository.updateMetaData(musicPublicId, newStoreName, musicFile.getSize());
        } catch (RuntimeException e) {
            if (newStoreName != null) {
                try {
                    musicFileService.deleteFile(newStoreName);
                } catch (RuntimeException e1) {
                    log.warn("Failed to rollback file: {}", newStoreName, e1);
                }
            }
            throw e;
        }
    }
}
