package com.treep.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ApiGatewayHttpHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String requestURI = httpServerExchange.getRequestURI();
        if (requestURI.split("&").length > 1) {
            System.out.println("contains");
        }
    }

}
