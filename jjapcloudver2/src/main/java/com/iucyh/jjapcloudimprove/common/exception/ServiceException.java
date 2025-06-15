package com.iucyh.jjapcloudimprove.common.exception;

import com.iucyh.jjapcloudimprove.common.exception.errorcode.ServiceErrorCode;

public class ServiceException extends RuntimeException {

    private final ServiceErrorCode code;

    public ServiceException(ServiceErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(ServiceErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
