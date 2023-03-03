package com.treep.util;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import static com.treep.util.HttpConstants.PROXYING_MESSAGE_REQUEST_TEMPLATE;
import static com.treep.util.HttpConstants.PROXYING_MESSAGE_RESPONSE_TEMPLATE;

@Slf4j
public class LoggerUtils {

    public static void logProxyingRequest(String requestMethod, String sourceEndpoint, URI targetUrl) {
        log.info(String.format(
                PROXYING_MESSAGE_REQUEST_TEMPLATE,
                requestMethod,
                sourceEndpoint,
                targetUrl
        ));
    }

    public static void logProxyingRequestResult(int statusCode) {
        log.info(String.format(PROXYING_MESSAGE_RESPONSE_TEMPLATE, statusCode));
    }
}
