package com.treep.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.treep.exception.RequestBuildingException;
import com.treep.exception.RequestExecutionException;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.model.GatewaySourceResponseDto;
import com.treep.model.GatewayTargetResponseDto;
import com.treep.builder.GatewayModelBuilder;
import com.treep.model.GatewayModel;
import com.treep.builder.RequestModelBuilder;
import com.treep.executor.RequestExecutor;
import com.treep.util.constants.HttpConstants;
import com.treep.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static com.treep.util.constants.HttpConstants.APPLICATION_JSON_W_CHARSET;
import static com.treep.util.constants.HttpConstants.CONTENT_TYPE;

@Slf4j
public class ApiGatewayHttpHandler implements HttpHandler {

    /**
     * Restricted headers for HTTP response
     */
    private static final Set<String> RESTRICTED_RESPONSE_HEADERS = Set.of("transfer-encoding");

    private final ObjectMapper objectMapper;

    private final RequestExecutor requestExecutor;

    public ApiGatewayHttpHandler(ObjectMapper om, RequestExecutor requestExecutor) {
        this.objectMapper = om;
        this.requestExecutor = requestExecutor;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.debug("+handle()");
        GatewayModel gatewayModel;
        HttpRequest targetRequest;
        GatewayTargetResponseDto responseDto;

        try {
            gatewayModel = GatewayModelBuilder.buildGatewayModel(exchange);
            targetRequest = RequestModelBuilder.buildHttpRequestModel(gatewayModel);

            LoggerUtils.logProxyingRequest(
                    targetRequest.method(),
                    exchange.getRequestURI().getRawPath(),
                    targetRequest.uri()
            );

            responseDto = requestExecutor.executeRequest(targetRequest);
        } catch (RouteDefinitionNotFoundException | RequestBuildingException | RequestExecutionException e) {
            handleProblemWhileRequestExecution(exchange, e.getMessage());
            log.error("-handle(): error while handling request", e);
            return;
        }

        LoggerUtils.logProxyingRequestResult(responseDto.httpStatusCode());

        sendProxyResponse(exchange, responseDto);
        log.debug("-handle()");
    }

    private void handleProblemWhileRequestExecution(HttpExchange exchange, String message) throws IOException {
        log.debug("+handleRouteDefinitionNotFound(): message: {}", message);
        byte[] responseErrorJson = serializeErrorMessageToJson(message).getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON_W_CHARSET);
        OutputStream exchangeOut = exchange.getResponseBody();
        exchange.sendResponseHeaders(
                HttpConstants.REQUEST_EXECUTION_PROBLEM_HTTP_STATUS_CODE,
                responseErrorJson.length);

        exchangeOut.write(responseErrorJson);
        exchangeOut.flush();
        exchangeOut.close();
        log.debug("-handleRouteDefinitionNotFound()");
    }

    private String serializeErrorMessageToJson(String messageContent) throws JsonProcessingException {
        log.debug("+prepareErrorMessage()");
        GatewaySourceResponseDto responseDto = new GatewaySourceResponseDto(messageContent);
        log.debug("-prepareErrorMessage()");
        return objectMapper.writeValueAsString(responseDto);
    }

    private void sendProxyResponse(HttpExchange exchange, GatewayTargetResponseDto responseDto) throws IOException {
        log.debug("+sendProxyResponse()");
        int statusCode = responseDto.httpStatusCode();
        Map<String, String> responseHeaders = responseDto.responseHeaders();
        byte[] responseBody = responseDto.responseBody();

        for (Map.Entry<String, String> e : responseHeaders.entrySet()) {
            if (!RESTRICTED_RESPONSE_HEADERS.contains(e.getKey().toLowerCase())) {
                exchange.getResponseHeaders().add(e.getKey(), e.getValue());
            }
        }

        if (responseBody == null) {
            exchange.sendResponseHeaders(statusCode, 0);
            exchange.close();
        } else {
            exchange.sendResponseHeaders(statusCode, responseBody.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody);
            os.flush();
            os.close();
            exchange.close();
        }
        log.debug("-sendProxyResponse()");
    }

}
