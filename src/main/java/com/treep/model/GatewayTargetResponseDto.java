package com.treep.model;

import java.util.Map;

public record GatewayTargetResponseDto(
        int httpStatusCode,
        byte[] responseBody,
        Map<String, String> responseHeaders
 ) { }
