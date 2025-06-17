package com.iucyh.jjapcloudimprove.dto;

import com.iucyh.jjapcloudimprove.common.exception.errorcode.ErrorCode;
import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

    private boolean isSuccess;
    private String code;
    private String message;
    private T data;

    private ResponseDto() {}

    public static <T> ResponseDto<T> success(T data) {
        ResponseDto<T> dto = new ResponseDto<>();
        dto.isSuccess = true;
        dto.code = null;
        dto.message = null;
        dto.data = data;
        return dto;
    }

    public static <T> ResponseDto<T> fail(ErrorCode errorCode, @Nullable String message) {
        ResponseDto<T> dto = new ResponseDto<>();
        dto.isSuccess = false;
        dto.code = errorCode.getCode();
        dto.message = message != null ? message : errorCode.getMessage();
        dto.data = null;
        return dto;
    }
}
