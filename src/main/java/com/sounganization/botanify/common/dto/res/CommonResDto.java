package com.sounganization.botanify.common.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

public record CommonResDto(
        Integer status,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long id
) {
    public CommonResDto(HttpStatus httpStatus, String message, Long id) {
        this(httpStatus.value(), message, id);
    }

    public CommonResDto(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), message, null);
    }
}
