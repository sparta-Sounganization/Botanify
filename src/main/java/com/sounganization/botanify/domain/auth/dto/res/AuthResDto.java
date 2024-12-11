package com.sounganization.botanify.domain.auth.dto.res;

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
