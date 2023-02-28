package com.treep.handler.processor;

import com.treep.handler.processor.model.GatewayModel;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class HttpServerExchangeProcessor {

    private final HttpServerExchange exchangeToProcess;

    public HttpServerExchangeProcessor(HttpServerExchange exchangeToProcess) {
        this.exchangeToProcess = exchangeToProcess;
    }

    public GatewayModel processExchange() throws IOException {
        if (exchangeToProcess == null) { return null; }
        return process();
    }

    private GatewayModel process() throws IOException {
        String httpMethod = getHttpMethod();
        Map<String, Deque<String>> queryString = getQueryParamsIfExists();
        byte[] requestBody = getRequestBodyIfExists();
        Map<String, String> requestHeaders = getHttpHeaders();
        String sourceEndpoint = getSourceEndpoint();

        return new GatewayModel(
                httpMethod,
                requestBody,
                requestHeaders,
                queryString,
                sourceEndpoint
        );
    }

    private Map<String, String> getHttpHeaders() {
        Map<String, String> result = new HashMap<>();

        exchangeToProcess.getRequestHeaders().forEach(k -> {
            result.put(k.getHeaderName().toString(), k.element());
        });

        return result;
    }

    private byte[] getRequestBodyIfExists() throws IOException {
        byte[] requestBytes = null;

        exchangeToProcess.startBlocking();
        InputStream requestInputStream = exchangeToProcess.getInputStream();
        if (requestInputStream != null) {
            requestBytes = requestInputStream.readAllBytes();
        }

        return requestBytes;
    }

    private String getHttpMethod() {
        return exchangeToProcess.getRequestMethod().toString();
    }

    private Map<String, Deque<String>> getQueryParamsIfExists() {
        return exchangeToProcess.getQueryParameters();
    }

    private String getSourceEndpoint() {
        return exchangeToProcess.getRequestURI();
    }
}
