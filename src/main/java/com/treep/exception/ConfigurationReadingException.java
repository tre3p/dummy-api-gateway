package com.treep.exception;

public class ConfigurationReadingException extends RuntimeException {

    public ConfigurationReadingException(String message) {
        super(message);
    }

    public ConfigurationReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
