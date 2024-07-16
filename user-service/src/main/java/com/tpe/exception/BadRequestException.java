package com.tpe.exception;

public class BadRequestException extends Throwable {
    public BadRequestException(String message) {
        super(message);
    }
}
