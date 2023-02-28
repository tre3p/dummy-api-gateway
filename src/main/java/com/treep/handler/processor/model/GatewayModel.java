package com.treep.handler.processor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.http.HttpHeaders;
import java.util.Deque;
import java.util.Map;

@Data
@AllArgsConstructor
public class GatewayModel {

    private String httpMethod;

    private byte[] requestBody;

    private Map<String, String> requestHeaders;

    private Map<String, Deque<String>> queryParams;
}
