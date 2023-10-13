package com.hust.bookstore.exception;

import com.hust.bookstore.enumration.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    protected final ResponseCode error;

    public BusinessException(ResponseCode error) {
        super(error.message());
        this.error = error;
    }

}
