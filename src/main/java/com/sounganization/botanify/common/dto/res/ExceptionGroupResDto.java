package com.sounganization.botanify.common.dto.res;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public record ExceptionGroupResDto(
        Integer status,
        Map<String, String> cases
) {
    public ExceptionGroupResDto(HttpStatus status) {
        this(status.value(), new HashMap<>());
    }

    public void addCase(String key, String value) {
        cases.put(key, value);
    }

}
