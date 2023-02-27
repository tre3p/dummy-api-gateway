package com.treep;

import com.treep.handler.ApiGatewayHttpHandler;
import io.undertow.Undertow;

public class Main {
    public static void main(String[] args) {
        Undertow s = Undertow.builder()
                .addHttpListener(8080, "0.0.0")
                .setHandler(new ApiGatewayHttpHandler())
                .build();

        s.start();
    }
}

