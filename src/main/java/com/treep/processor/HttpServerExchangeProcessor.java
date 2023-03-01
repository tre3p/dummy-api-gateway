package com.treep.processor;

import com.sun.net.httpserver.HttpExchange;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.model.GatewayModel;
import com.treep.storage.RouteDefinitionStorage;
import com.treep.util.ErrorConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HttpServerExchangeProcessor {

    private final HttpExchange httpExchange;

    public HttpServerExchangeProcessor(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public GatewayModel processExchange() throws IOException {
        log.debug("+processExchange()");
        String uri = httpExchange.getRequestURI().getRawPath();

        if (RouteDefinitionStorage.getRouteDefinitionBySourceEndpoint(uri) == null) {
            log.error("-processExchange(): RouteDefinition at {} not found", uri);
            throw new RouteDefinitionNotFoundException(
                    String.format(ErrorConstants.SOURCE_ENDPOINT_NOT_FOUND_ERROR_TEMPLATE, uri)
            );
        }

        GatewayModel processedExchangeResult = process();

        log.debug("-processExchange()");
        return processedExchangeResult;
    }

    private GatewayModel process() throws IOException {
        log.debug("+process()");
        String httpMethod = getHttpMethod();
        String queryString = getQueryParamsIfExists();
        byte[] requestBody = getRequestBodyIfExists();
        Map<String, String> requestHeaders = getHttpHeaders();

        GatewayModel result = new GatewayModel(
                httpMethod,
                requestBody,
                requestHeaders,
                queryString
        );

        log.debug("-process(): result: {}", result);
        return result;
    }

    private Map<String, String> getHttpHeaders() {
        log.debug("+getHttpHeaders()");
        Map<String, String> requestHeaders = httpExchange.getRequestHeaders().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get(0)));

        log.debug("-getHttpHeaders(): requestHeaders size: {}", requestHeaders.size());
        return requestHeaders;
    }

    private byte[] getRequestBodyIfExists() throws IOException {
        log.debug("+getRequestBodyIfExists()");
        InputStream is = httpExchange.getRequestBody();
        byte[] bodyBytes = is.readAllBytes();

        log.debug("-getRequestBodyIfExists(): bodyBytes length: {}", bodyBytes.length);
        return bodyBytes;
    }

    private String getHttpMethod() {
        log.debug("+getHttpMethod()");
        String httpMethod = httpExchange.getRequestMethod();
        log.debug("-getHttpMethod(): httpMethod: {}", httpMethod);
        return httpMethod;
    }

    private String getQueryParamsIfExists() {
        log.debug("getQueryParamsIfExists()");
        return httpExchange.getRequestURI().getRawQuery();
    }
}
