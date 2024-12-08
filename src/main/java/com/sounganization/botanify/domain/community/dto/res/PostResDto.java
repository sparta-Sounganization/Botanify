package com.sounganization.botanify.domain.community.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class PostResDto {
    private final Integer status;
    private final String message;
    private final Long postId;

    public static ResponseEntity<PostResDto> toResponseEntity(HttpStatus status, String message, Long postId) {
        return new ResponseEntity<>(new PostResDto(status.value(), message, postId), status);
    }
}
