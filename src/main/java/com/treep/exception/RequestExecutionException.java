package com.treep.exception;

public class RequestExecutionException extends RuntimeException {

    public RequestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
