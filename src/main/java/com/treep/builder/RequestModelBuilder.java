package com.treep.builder;

import com.treep.exception.RequestBuildingException;
import com.treep.model.GatewayModel;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.treep.util.constants.ErrorConstants.ERROR_BUILDING_REQUEST_MODEL;

@Slf4j
public class RequestModelBuilder {

    public static HttpRequest buildHttpRequestModel(GatewayModel requestModel) {
        log.debug("+buildRequestModel()");
        String url = buildUrl(requestModel);
        Map<String, String> requestHeaders = requestModel.requestHeaders();
        byte[] requestBody = requestModel.requestBody();
        int requestTimeout = requestModel.requestTimeout();
        URI builtUrl;

        try {
            builtUrl = new URI(url);
        } catch (URISyntaxException e) {
            log.error("-buildHttpRequestModel(): error while building request model", e);
            throw new RequestBuildingException(ERROR_BUILDING_REQUEST_MODEL, e);
        }

        HttpRequest.Builder req = HttpRequest.newBuilder();
        req.uri(builtUrl);
        injectHeader(requestHeaders, req);
        determineAndInjectRequestMethod(req, requestModel.httpMethod(), requestBody);
        injectTimeout(req, requestTimeout);

        HttpRequest builtRequest = req.build();
        log.debug("-buildHttpRequestModel()");
        return builtRequest;
    }

    private static void injectHeader(Map<String, String> headers, HttpRequest.Builder builder) {
        log.debug("injectHeader()");
        headers.forEach((headerName, headerValue) -> builder.setHeader(headerName, headerValue));
    }

    private static String buildUrl(GatewayModel requestModel) {
        log.debug("+buildUrl()");
        String host = requestModel.targetHost();

        if (requestModel.targetQueryParams() != null && !requestModel.targetQueryParams().isEmpty()) {
            host += "?" + requestModel.targetQueryParams();
        }

        log.debug("-buildUrl()");
        return host;
    }

    private static void determineAndInjectRequestMethod(
            HttpRequest.Builder requestBuilder,
            String requestMethod, byte[] requestBody)
    {
        log.debug("+determineAndInjectRequestMethod():");
        switch (requestMethod.toUpperCase()) {
            case "GET" -> requestBuilder.GET();
            case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(requestBody));
            case "PUT" -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofByteArray(requestBody));
            case "DELETE" -> requestBuilder.DELETE();
            default -> throw new RequestBuildingException(String.format(
                    ERROR_BUILDING_REQUEST_MODEL,
                    "Unsupported HTTP method: " + requestMethod.toUpperCase()
                    ));
        }
        log.debug("-determineAndInjectRequestMethod()");
    }

    private static void injectTimeout(HttpRequest.Builder builder, int timeout) {
        log.debug("injectTimeout()");
        builder.timeout(Duration.of(timeout, ChronoUnit.SECONDS));
    }
}
