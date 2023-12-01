package com.hust.bookstore.exception;

import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.enumration.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static com.hust.bookstore.enumration.ResponseCode.BAD_REQUEST;

@ControllerAdvice
@Slf4j
public class BusinessExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleSystemConfigNotFound(BusinessException ex, WebRequest request) {
        log.info(ex.getMessage());

        return new ResponseEntity<>(
                BaseResponse.builder()
                        .code(ex.getError().code())
                        .message(ex.getError().message())
                        .build()
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleSystemConfigNotFound(Exception ex, WebRequest request) {
        log.info(ex.getMessage());

        return new ResponseEntity<>(
                BaseResponse.builder()
                        .code(BAD_REQUEST.code())
                        .message(ex.getMessage())
                        .build()
                , HttpStatus.BAD_REQUEST);
    }

    //handle exception when user not login
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleSystemConfigNotFound(InsufficientAuthenticationException ex, WebRequest request) {
        log.info(ex.getMessage());

        return new ResponseEntity<>(
                BaseResponse.builder()
                        .code(ResponseCode.UNAUTHORIZED.code())
                        .message(ex.getMessage())
                        .build()
                , HttpStatus.UNAUTHORIZED);
    }

    //handle exception when system error
    @ExceptionHandler(InternalError.class)
    public ResponseEntity<BaseResponse<Object>> handleSystemConfigNotFound(InternalError ex, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                BaseResponse.builder()
                        .code(ResponseCode.FAIL.code())
                        .message(ex.getMessage())
                        .build()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
