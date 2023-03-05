package com.treep.builder;

import com.sun.net.httpserver.HttpExchange;
import com.treep.config.model.RouteDefinition;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.model.GatewayModel;
import com.treep.storage.RouteDefinitionStorage;
import com.treep.util.constants.ErrorConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class GatewayModelBuilder {

    /**
     * Set of disallowed HTTP headers, which are not acceptable by {@link java.net.http.HttpRequest.Builder}
     */
    private static final Set<String> DISALLOWED_HEADERS_SET = Set.of(
            "connection",
            "content-length",
            "expect",
            "host",
            "upgrade"
    );

    public static GatewayModel buildGatewayModel(HttpExchange httpExchange) throws IOException {
        log.debug("+processExchange()");
        String uri = httpExchange.getRequestURI().getRawPath();
        RouteDefinition routeDefinition = RouteDefinitionStorage.getRouteDefinitionBySourceEndpoint(uri);

        if (routeDefinition == null) {
            log.error("-processExchange(): RouteDefinition at {} not found", uri);
            throw new RouteDefinitionNotFoundException(
                    String.format(ErrorConstants.SOURCE_ENDPOINT_NOT_FOUND_ERROR_TEMPLATE, uri)
            );
        }

        GatewayModel processedExchangeResult = process(routeDefinition, httpExchange);

        log.debug("-processExchange()");
        return processedExchangeResult;
    }

    private static GatewayModel process(RouteDefinition routeDefinition, HttpExchange httpExchange) throws IOException {
        log.debug("+process()");
        String httpMethod = getHttpMethod(httpExchange);
        String queryString = getQueryParams(httpExchange);
        byte[] requestBody = getRequestBody(httpExchange);
        Map<String, String> requestHeaders = getHttpHeaders(httpExchange);
        String targetUrl = routeDefinition.getTargetUrl();
        int requestTimeout = routeDefinition.getRequestTimeout();

        GatewayModel result = new GatewayModel(
                httpMethod,
                requestBody,
                requestHeaders,
                queryString,
                targetUrl,
                requestTimeout
        );

        log.debug("-process(): result: {}", result);
        return result;
    }

    private static Map<String, String> getHttpHeaders(HttpExchange httpExchange) {
        log.debug("+getHttpHeaders()");
        Map<String, String> requestHeaders = httpExchange.getRequestHeaders().entrySet().stream()
                .filter(k -> !DISALLOWED_HEADERS_SET.contains(k.getKey().toLowerCase()))
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get(0)));

        log.debug("-getHttpHeaders(): requestHeaders size: {}", requestHeaders.size());
        return requestHeaders;
    }

    private static byte[] getRequestBody(HttpExchange httpExchange) throws IOException {
        log.debug("+getRequestBodyIfExists()");
        InputStream is = httpExchange.getRequestBody();
        byte[] bodyBytes = is.readAllBytes();

        log.debug("-getRequestBodyIfExists(): bodyBytes length: {}", bodyBytes.length);
        return bodyBytes;
    }

    private static String getHttpMethod(HttpExchange httpExchange) {
        log.debug("+getHttpMethod()");
        String httpMethod = httpExchange.getRequestMethod();
        log.debug("-getHttpMethod(): httpMethod: {}", httpMethod);
        return httpMethod;
    }

    private static String getQueryParams(HttpExchange httpExchange) {
        log.debug("getQueryParamsIfExists()");
        return httpExchange.getRequestURI().getRawQuery();
    }
}
