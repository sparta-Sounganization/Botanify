package com.sounganization.botanify.common.dto.res;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ExceptionGroupResDto {
    private final Integer status;
    private final Map<String, String> cases;

    public ExceptionGroupResDto(HttpStatus status) {
        this.status = status.value();
        this.cases = new HashMap<>();
    }

    public void addCase(String key, String value) {
        cases.put(key, value);
    }

}
