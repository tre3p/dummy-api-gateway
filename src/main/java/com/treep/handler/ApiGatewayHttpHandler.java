package com.treep.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.model.GatewayResponseDto;
import com.treep.processor.HttpServerExchangeProcessor;
import com.treep.model.GatewayModel;
import com.treep.util.HttpConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.treep.util.HttpConstants.APPLICATION_JSON_W_CHARSET;
import static com.treep.util.HttpConstants.CONTENT_TYPE;

@Slf4j
public class ApiGatewayHttpHandler implements HttpHandler {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.debug("+handle()");
        HttpServerExchangeProcessor exp = new HttpServerExchangeProcessor(exchange);
        GatewayModel m;

        try {
            m = exp.processExchange();
        } catch (RouteDefinitionNotFoundException e) {
            handleRouteDefinitionNotFound(exchange, e.getMessage());
            log.error("-handle(): error while handling request", e);
            return;
        }
        log.debug("-handle()");
    }

    private void handleRouteDefinitionNotFound(HttpExchange exchange, String message) throws IOException {
        log.debug("+handleRouteDefinitionNotFound(): message: {}", message);
        byte[] responseErrorJson = prepareErrorMessage(message).getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON_W_CHARSET);
        OutputStream exchangeOut = exchange.getResponseBody();
        exchange.sendResponseHeaders(
                HttpConstants.ROUTE_DEFINITION_NOT_FOUND_HTTP_STATUS_CODE,
                responseErrorJson.length);

        exchangeOut.write(responseErrorJson);
        exchangeOut.flush();
        exchangeOut.close();
        log.debug("-handleRouteDefinitionNotFound()");
    }

    private String prepareErrorMessage(String messageContent) throws JsonProcessingException {
        log.debug("+prepareErrorMessage()");
        GatewayResponseDto responseDto = new GatewayResponseDto(messageContent);
        log.debug("-prepareErrorMessage()");
        return om.writeValueAsString(responseDto);
    }

}
