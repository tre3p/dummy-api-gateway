package com.treep.model;

import java.util.Map;

public record GatewayModel(
        String httpMethod,
        byte[] requestBody,
        Map<String, String> requestHeaders,
        String targetQueryParams,
        String targetHost,
        int requestTimeout
) {}
