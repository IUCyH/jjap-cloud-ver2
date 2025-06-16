package com.iucyh.jjapcloudimprove.service.music;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.common.util.file.FileMimeType;
import com.iucyh.jjapcloudimprove.common.util.file.FileStorageService;
import com.iucyh.jjapcloudimprove.common.util.file.LimitedInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicFileService {

    private static final long MUSIC_BITRATE = 320000;
    private final FileStorageService fileService;

    public InputStreamResource streamFile(String storeName, long start, long end) {
        try {
            LimitedInputStream result = fileService.stream(storeName, start, end);
            return new InputStreamResource(result);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_STREAM_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Don't use it directly, Please use it through (MusicFacade).save method.
     * @param file
     * @return Result after store a music
     */
    public MusicFileStoreResult storeFile(MultipartFile file) {
        checkMimeType(file);

        try {
            String storeName = fileService.store(file);
            long playTime = calculatePlayTime(file.getSize());

            return new MusicFileStoreResult(storeName, playTime);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_UPLOAD_ERROR, e.getMessage(), e);
        }
    }

    public String replaceFile(MultipartFile file, String storeName) {
        checkMimeType(file);

        String newStoreName = null;
        try {
            newStoreName = fileService.store(file);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_UPLOAD_ERROR, e.getMessage(), e);
        }

        try {
            fileService.delete(storeName);
            return newStoreName;
        } catch (RuntimeException e) {
            if (newStoreName != null) {
                try {
                    fileService.delete(newStoreName);
                } catch (RuntimeException e1) {
                    log.warn("Failed to rollback file: {}", newStoreName, e1);
                }
            }
            throw new ServiceException(ServiceErrorCode.MUSIC_DELETE_ERROR, e.getMessage(), e);
        }
    }

    public void deleteFile(String storeName) {
        try {
            fileService.delete(storeName);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_DELETE_ERROR, e.getMessage(), e);
        }
    }

    private void checkMimeType(MultipartFile file) {
        if (!fileService.checkMimeType(file, FileMimeType.MP3)) {
            throw new ServiceException(ServiceErrorCode.MUSIC_INVALID_MIME_TYPE, "Invalid file type");
        }
    }

    private long calculatePlayTime(long size) {
        return size * 8 / MUSIC_BITRATE;
    }
}
