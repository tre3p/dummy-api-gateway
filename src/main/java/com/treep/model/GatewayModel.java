package com.treep.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class GatewayModel {

    private String httpMethod;

    private byte[] requestBody;

    private Map<String, String> requestHeaders;

    private String queryParams;
}
