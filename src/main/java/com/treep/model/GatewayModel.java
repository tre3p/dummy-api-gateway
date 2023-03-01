package com.treep.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayModel {

    private String httpMethod;

    private byte[] requestBody;

    private Map<String, String> requestHeaders;

    private String targetQueryParams;

    private String targetHost;

    private int requestTimeout;

}
