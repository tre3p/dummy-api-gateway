package com.treep.model;

import lombok.Data;

import java.util.Map;

@Data
public class GatewayTargetResponseDto {

    private int httpStatusCode;

    private byte[] responseBody;

    private Map<String, String> responseHeaders;

}
