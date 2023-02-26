package com.treep.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RouteDefinition {

    @JsonProperty("target-url")
    private String targetUrl;

    @JsonProperty("source-endpoint")
    private String sourceEndpoint;

    @JsonProperty("request-timeout")
    private int requestTimeout;

    @JsonProperty("fail-status-code")
    private int failStatusCode;

}
