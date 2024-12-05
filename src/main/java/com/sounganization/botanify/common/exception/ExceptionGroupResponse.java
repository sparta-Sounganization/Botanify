package com.sounganization.botanify.common.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ExceptionGroupResponse {
    private Integer status;
    private final Map<String, String> cases;

    public ExceptionGroupResponse(HttpStatus status) {
        this.status = status.value();
        this.cases = new HashMap<>();
    }

    public void addCase(String key, String value) {
        cases.put(key, value);
    }

}
