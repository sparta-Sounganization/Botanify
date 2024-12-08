package com.sounganization.botanify.common.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class CustomException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(CustomException.class);

    private final ExceptionStatus status;

    public CustomException(ExceptionStatus status) {
        super(status.getMessage());
        this.status = status;
        logError();
    }

    public CustomException(ExceptionStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
        logError();
    }

    private void logError() {
        logger.error("예외 발생: {} - {}", status.getStatus(), status.getMessage(), this);
    }
}
