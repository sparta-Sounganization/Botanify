package com.sounganization.botanify.common.exception;

import com.sounganization.botanify.common.dto.res.ExceptionResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public enum ExceptionStatus {
    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 알 수 없는 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"잘못된 요청입니다."),
    BODY_NOT_FOUND(HttpStatus.BAD_REQUEST,"요청 본문을 찾을 수 없습니다."),

    // token
    TOKEN_NOT_PROVIDED(HttpStatus.UNAUTHORIZED, "토큰이 제공되지 않았습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    // auth
    DUPLICATED_EMAIL(HttpStatus.CONFLICT,"이미 존재하는 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    INVALID_ROLE(HttpStatus.UNAUTHORIZED,"유효하지 않은 사용자 권한입니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED,"로그인이 필요합니다."),
    ACCOUNT_DELETED(HttpStatus.FORBIDDEN, "해당 계정은 삭제되었습니다."),
    USER_DETAILS_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자 정보를 찾을 수 없습니다."),

    // user
    DELETED_USER(HttpStatus.FORBIDDEN,"탈퇴된 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
    INVALID_UPDATE_REQUEST(HttpStatus.UNAUTHORIZED,"수정할 정보가 없습니다."),

    // plant
    PLANT_NOT_FOUND(HttpStatus.NOT_FOUND,"식물을 찾을 수 없습니다."),
    PLANT_NOT_OWNED(HttpStatus.UNAUTHORIZED, "식물의 주인이 아닙니다."),

    // diary
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "성장 일지를 찾을 수 없습니다."),
    DIARY_NOT_OWNED(HttpStatus.UNAUTHORIZED, "성장 일지의 주인이 아닙니다."),
    // post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."),

    // comment
    INVALID_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해주세요."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "댓글 작성자만 댓글을 수정하거나 삭제할 수 있습니다.")

    ;
    private final HttpStatus status;
    private final String message;

    public ResponseEntity<ExceptionResDto> toResponseEntity() {
        return ResponseEntity.status(this.status).body(new ExceptionResDto(this.status.value(), this.message));
    }
}
