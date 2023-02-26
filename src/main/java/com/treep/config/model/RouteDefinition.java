package com.treep.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POJO for storing information about one route definition
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RouteDefinition {

    /**
     * URL to which request will be sent
     */
    @JsonProperty("target-url")
    private String targetUrl;

    /**
     * Endpoint on api-gateway on which request is received
     */
    @JsonProperty("source-endpoint")
    private String sourceEndpoint;

    /**
     * Timeout for request proxying
     */
    @JsonProperty("request-timeout")
    private int requestTimeout;

    /**
     * Status code for api-gateway fails
     */
    @JsonProperty("fail-status-code")
    private int failStatusCode;

}
