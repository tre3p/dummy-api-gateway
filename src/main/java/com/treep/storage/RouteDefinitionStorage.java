package com.treep.storage;

import com.treep.config.model.RouteDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public final class RouteDefinitionStorage {

    private static Map<String, RouteDefinition> sourceEndpointToRouteDefinitionStorage;

    private RouteDefinitionStorage() { }

    public static void setRouteDefinitionStorage(Map<String, RouteDefinition> storage) {
        log.debug("setRouteDefinitionStorage(): storage size: {}", storage.size());
        sourceEndpointToRouteDefinitionStorage = storage;
    }

    public static RouteDefinition getRouteDefinitionBySourceEndpoint(String sourceEndpoint) {
        log.debug("+getRouteDefinitionBySourceEndpoint(): looking for endpoint: {}", sourceEndpoint);
        RouteDefinition result = sourceEndpointToRouteDefinitionStorage.get(sourceEndpoint);
        log.debug("-getRouteDefinitionBySourceEndpoint(): result: {}", result);
        return result;
    }

}
