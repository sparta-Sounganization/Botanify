package com.sounganization.botanify.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        log.error("처리되지 않은 런타임 예외 : {}", ex.getMessage(), ex);
        return ExceptionStatus.INTERNAL_SERVER_ERROR.toResponseEntity();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("Null 포인터 참조 예외", ex);
        return ExceptionResponse.toResponseEntityWith(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 존재하지 않는 값을 참조하였습니다.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.info("요청 본문의 누락을 감지");
        return ExceptionStatus.BODY_NOT_FOUND.toResponseEntity();
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex) {
        log.info("서비스에서 잘못된 동작을 감지");
        return ex.getStatus().toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info("유효하지 않은 요청 본문을 감지");
        ExceptionGroupResponse res = new ExceptionGroupResponse(HttpStatus.BAD_REQUEST);
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            res.addCase(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(res);
    }
}
