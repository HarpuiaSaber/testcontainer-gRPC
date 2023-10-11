package com.toannq.test.commons.exception;

public record BusinessErrorCode(int code, String message, int httpStatus) {
    public BusinessErrorCode(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
