package com.sounganization.botanify.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionStatus status;

    public CustomException(ExceptionStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public CustomException(ExceptionStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }

}
