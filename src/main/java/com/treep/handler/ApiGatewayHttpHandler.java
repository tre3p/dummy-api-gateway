package com.treep.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.treep.exception.RouteDefinitionNotFoundException;
import com.treep.handler.processor.HttpServerExchangeProcessor;
import com.treep.handler.processor.model.GatewayModel;

import java.io.IOException;

public class ApiGatewayHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpServerExchangeProcessor exp = new HttpServerExchangeProcessor(exchange);
        GatewayModel m = null;

        try {
            m = exp.processExchange();
        } catch (RouteDefinitionNotFoundException e) {
            // TODO: insert handler for gateway error
        }
    }

}
