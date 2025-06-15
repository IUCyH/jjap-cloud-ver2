package com.iucyh.jjapcloudimprove.service.music;

import com.iucyh.jjapcloudimprove.common.exception.ServiceException;
import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;
import com.iucyh.jjapcloudimprove.common.util.file.FileMimeType;
import com.iucyh.jjapcloudimprove.common.util.file.FileStorageService;
import com.iucyh.jjapcloudimprove.common.util.file.LimitedInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MusicFileService {

    private final FileStorageService fileService;

    public InputStreamResource streamFile(String storeName, long start, long end) {
        try {
            LimitedInputStream result = fileService.stream(storeName, start, end);
            return new InputStreamResource(result);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_STREAM_ERROR, e.getMessage(), e);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            if (!fileService.checkMimeType(file, FileMimeType.MP3)) {
                throw new ServiceException(ServiceErrorCode.MUSIC_INVALID_MIME_TYPE, "Invalid file type");
            }

            return fileService.store(file);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_UPLOAD_ERROR, e.getMessage(), e);
        }
    }

    public void deleteFile(String storeName) {
        try {
            fileService.delete(storeName);
        } catch (RuntimeException e) {
            throw new ServiceException(ServiceErrorCode.MUSIC_DELETE_ERROR, e.getMessage(), e);
        }
    }
}
