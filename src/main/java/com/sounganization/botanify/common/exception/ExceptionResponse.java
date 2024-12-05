package com.sounganization.botanify.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ExceptionResponse (
        Integer status,
        String message
) {
    public static ResponseEntity<ExceptionResponse> toResponseEntityWith(HttpStatus status, String message) {
        return new ResponseEntity<>(new ExceptionResponse(status.value(), message), status);
    }
}
