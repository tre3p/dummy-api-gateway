package com.treep.exception;

public class RequestBuildingException extends RuntimeException {

    public RequestBuildingException(String message) {
        super(message);
    }

    public RequestBuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}
