package com.treep.processor;

import com.treep.exception.RequestExecutionException;
import com.treep.model.GatewayTargetResponseDto;
import lombok.extern.slf4j.Slf4j;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.treep.util.ErrorConstants.ERROR_EXECUTING_REQUEST;

@Slf4j
public class RequestExecutor {

    private static HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public static GatewayTargetResponseDto executeRequest(HttpRequest request) {
        log.debug("+executeRequest():");
        HttpResponse<byte[]> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception e) {
            log.error("-executeRequest(): error while executing request", e);
            throw new RequestExecutionException(ERROR_EXECUTING_REQUEST, e);
        }

        GatewayTargetResponseDto responseDto = processResponse(response);
        log.debug("-executeRequest()");
        return responseDto;
    }

    private static GatewayTargetResponseDto processResponse(HttpResponse<byte[]> response) {
        log.debug("+processResponse()");
        Map<String, String> responseHeaders = convertHeaders(response.headers());
        System.out.println(responseHeaders);
        int responseStatus = response.statusCode();
        byte[] responseBody = null;

        if (response.body() != null) {
            responseBody = response.body();
        }

        System.out.println(new String(responseBody, StandardCharsets.UTF_8));

        GatewayTargetResponseDto responseDto = new GatewayTargetResponseDto();
        responseDto.setResponseHeaders(responseHeaders);
        responseDto.setHttpStatusCode(responseStatus);
        responseDto.setResponseBody(responseBody);

        log.debug("-processResponse()");
        return responseDto;
    }

    private static Map<String, String> convertHeaders(HttpHeaders headers) {
        log.debug("+convertHeaders()");
        Map<String, String> result = new HashMap<>();

        for (var entry : headers.map().entrySet()) {
            result.put(entry.getKey(), entry.getValue().get(0));
        }

        log.debug("-convertHeaders()");
        return result;
    }
}
