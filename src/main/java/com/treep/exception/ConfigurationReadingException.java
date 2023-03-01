package com.treep.exception;

/**
 * Exception which thrown on any issue with reading/validating routes definitions
 */
public class ConfigurationReadingException extends Exception {

    public ConfigurationReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
