package com.treep.storage;

import com.treep.config.model.RouteDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class RouteDefinitionStorage {

    private static Map<String, RouteDefinition> routeDefinitionStorage;

    public static void setRouteDefinitionStorage(Map<String, RouteDefinition> storage) {
        log.debug("setRouteDefinitionStorage(): storage size: {}", storage.size());
        routeDefinitionStorage = storage;
    }

    public static RouteDefinition getRouteDefinitionBySourceEndpoint(String sourceEndpoint) {
        log.debug("+getRouteDefinitionBySourceEndpoint(): looking for endpoint: {}", sourceEndpoint);
        RouteDefinition result = routeDefinitionStorage.get(sourceEndpoint);
        log.debug("-getRouteDefinitionBySourceEndpoint(): result: {}", result);
        return result;
    }

}
