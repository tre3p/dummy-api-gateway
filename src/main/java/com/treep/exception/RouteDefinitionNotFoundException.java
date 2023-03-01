package com.treep.exception;

public class RouteDefinitionNotFoundException extends RuntimeException {
    public RouteDefinitionNotFoundException(String message) {
        super(message);
    }
}
