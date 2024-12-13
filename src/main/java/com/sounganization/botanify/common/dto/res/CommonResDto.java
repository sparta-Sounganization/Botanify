package com.sounganization.botanify.common.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

public record CommonResDto(
        Integer status,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long id,
        @JsonIgnore
        String token
) {
    public CommonResDto(HttpStatus httpStatus, String message, Long id) {
        this(httpStatus.value(), message, id, null);
    }

    public CommonResDto(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), message, null, null);
    }

    public CommonResDto(HttpStatus httpStatus, String message, String token) {
        this(httpStatus.value(), message, null, token);
    }
}
