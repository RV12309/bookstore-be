package com.hust.bookstore.exception;

import com.hust.bookstore.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

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
}
