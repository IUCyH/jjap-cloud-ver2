package com.iucyh.jjapcloudimprove.common.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceErrorCode implements ErrorCode {

    // MUSIC
    MUSIC_NOT_FOUND(HttpStatus.NOT_FOUND, "MUSIC-001", "Music not found"),
    MUSIC_INVALID_MIME_TYPE(HttpStatus.BAD_REQUEST, "MUSIC-002", "Invalid music mime type"),
    MUSIC_STREAM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MUSIC-003", "Music stream error"),
    MUSIC_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MUSIC-004", "Music upload error"),
    MUSIC_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MUSIC-005", "Music delete error"),

    // RANGE
    INVALID_RANGE_FORMAT(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "RANGE-001", "Invalid range format");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
