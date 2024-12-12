package com.sounganization.botanify.domain.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResDto(
        int status,
        String message,
        Long id,
        @JsonIgnore
        String token
) {
    // 회원가입용 생성자(token 이 필요 없을 때)
    public AuthResDto(int status, String message, Long id) {
        this(status, message, id, null);
    }

    // 로그인용 생성자 (id 가 필요 없을 때)
    public AuthResDto(int status, String message, String token) {
        this(status, message, null, token);
    }
}
