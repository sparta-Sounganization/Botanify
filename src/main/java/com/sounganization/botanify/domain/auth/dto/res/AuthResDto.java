package com.sounganization.botanify.domain.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResDto(
        int status,
        String message,
        Long id
) {
    // 로그인용 생성자 (id가 필요 없을 때)
    public AuthResDto(int status, String message) {
        this(status, message, null);
    }
}
