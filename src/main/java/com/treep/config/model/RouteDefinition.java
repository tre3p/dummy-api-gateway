package com.treep.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RouteDefinition {

    @JsonProperty("target-url")
    private String targetUrl;

    @JsonProperty("source-endpoint")
    private String sourceEndpoint;

    @JsonProperty("request-timeout")
    private int requestTimeout;

    @JsonProperty("fail-status-code")
    private int failStatusCode;

    public RouteDefinition(String targetUrl, String sourceEndpoint, int requestTimeout, int failStatusCode) {
        this.targetUrl = targetUrl;
        this.sourceEndpoint = sourceEndpoint;
        this.requestTimeout = requestTimeout;
        this.failStatusCode = failStatusCode;
    }

    public RouteDefinition() {
    }

    public String getSourceEndpoint() {
        return sourceEndpoint;
    }

    public void setSourceEndpoint(String sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getFailStatusCode() {
        return failStatusCode;
    }

    public void setFailStatusCode(int failStatusCode) {
        this.failStatusCode = failStatusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteDefinition that = (RouteDefinition) o;
        return requestTimeout == that.requestTimeout &&
                failStatusCode == that.failStatusCode &&
                Objects.equals(targetUrl, that.targetUrl) &&
                Objects.equals(sourceEndpoint, that.sourceEndpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetUrl, sourceEndpoint, requestTimeout, failStatusCode);
    }
}
