package com.sounganization.botanify.common.dto.res;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ExceptionResDto(
        Integer status,
        String message
) {
    public static ResponseEntity<ExceptionResDto> toResponseEntityWith(HttpStatus status, String message) {
        return new ResponseEntity<>(new ExceptionResDto(status.value(), message), status);
    }
}
