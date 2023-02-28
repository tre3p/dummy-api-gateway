package com.treep.handler;

import com.treep.handler.processor.HttpServerExchangeProcessor;
import com.treep.handler.processor.model.GatewayModel;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ApiGatewayHttpHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        HttpServerExchangeProcessor p = new HttpServerExchangeProcessor(httpServerExchange);
        GatewayModel g = p.processExchange();
        System.out.println(g);
    }

}
