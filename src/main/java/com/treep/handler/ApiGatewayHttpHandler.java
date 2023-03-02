package com.treep.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.treep.exception.RequestExecutionException;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.model.GatewaySourceResponseDto;
import com.treep.model.GatewayTargetResponseDto;
import com.treep.processor.GatewayModelBuilder;
import com.treep.model.GatewayModel;
import com.treep.processor.RequestModelBuilder;
import com.treep.processor.RequestExecutor;
import com.treep.util.HttpConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static com.treep.util.HttpConstants.APPLICATION_JSON_W_CHARSET;
import static com.treep.util.HttpConstants.CONTENT_TYPE;
import static com.treep.util.HttpConstants.PROXYING_MESSAGE_REQUEST_TEMPLATE;
import static com.treep.util.HttpConstants.PROXYING_MESSAGE_RESPONSE_TEMPLATE;

@Slf4j
public class ApiGatewayHttpHandler implements HttpHandler {

    private static final ObjectMapper OM = new ObjectMapper();

    private static final Set<String> RESTRICTED_RESPONSE_HEADERS = Set.of("transfer-encoding");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.debug("+handle()");
        GatewayModel gatewayModel;

        try {
            gatewayModel = GatewayModelBuilder.processExchange(exchange);
        } catch (RouteDefinitionNotFoundException e) {
            handleProblemWhileRequestExecution(exchange, e.getMessage());
            log.error("-handle(): error while handling request", e);
            return;
        }

        HttpRequest targetRequest = RequestModelBuilder.buildHttpRequestModel(gatewayModel);
        log.info(String.format(
                PROXYING_MESSAGE_REQUEST_TEMPLATE,
                targetRequest.method(),
                exchange.getRequestURI().getRawPath(),
                targetRequest.uri()
                ));

        GatewayTargetResponseDto responseDto;
        try {
            responseDto = RequestExecutor.executeRequest(targetRequest);
        } catch (RequestExecutionException e) {
            handleProblemWhileRequestExecution(exchange, e.getMessage());
            return;
        }

        log.info(String.format(PROXYING_MESSAGE_RESPONSE_TEMPLATE, responseDto.getHttpStatusCode()));

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
        return OM.writeValueAsString(responseDto);
    }

    private void sendProxyResponse(HttpExchange exchange, GatewayTargetResponseDto responseDto) throws IOException {
        log.debug("+sendProxyResponse()");
        int statusCode = responseDto.getHttpStatusCode();
        Map<String, String> responseHeaders = responseDto.getResponseHeaders();
        byte[] responseBody = responseDto.getResponseBody();

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
