package com.treep.handler.processor;

import com.sun.net.httpserver.HttpExchange;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.handler.processor.model.GatewayModel;
import com.treep.storage.RouteDefinitionStorage;
import com.treep.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpServerExchangeProcessor {

    private final HttpExchange httpExchange;

    public HttpServerExchangeProcessor(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public GatewayModel processExchange() throws IOException {
        String uri = httpExchange.getRequestURI().getRawPath();
        System.out.println(uri);

        if (RouteDefinitionStorage.getRouteDefinitionBySourceEndpoint(uri) == null) {
            throw new RouteDefinitionNotFoundException(
                    String.format(Constants.SOURCE_ENDPOINT_NOT_FOUND_ERROR_TEMPLATE, uri)
            );
        }

        return process();
    }

    private GatewayModel process() throws IOException {
        String httpMethod = getHttpMethod();
        String queryString = getQueryParamsIfExists();
        byte[] requestBody = getRequestBodyIfExists();
        Map<String, String> requestHeaders = getHttpHeaders();

        return new GatewayModel(
                httpMethod,
                requestBody,
                requestHeaders,
                queryString
        );
    }

    private Map<String, String> getHttpHeaders() {
        Map<String, String> requestHeaders = httpExchange.getRequestHeaders().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get(0)));

        return requestHeaders;
    }

    private byte[] getRequestBodyIfExists() throws IOException {
        InputStream is = httpExchange.getRequestBody();

        return is.readAllBytes();
    }

    private String getHttpMethod() {
        return httpExchange.getRequestMethod();
    }

    private String getQueryParamsIfExists() {
        String requestUri = httpExchange.getRequestURI().getRawQuery();

        return requestUri;
    }
}
