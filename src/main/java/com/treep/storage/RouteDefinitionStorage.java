package com.treep.storage;

import com.treep.config.model.RouteDefinition;

import java.util.Map;

public class RouteDefinitionStorage {

    private static Map<String, RouteDefinition> routeDefinitionStorage;

    public static void setRouteDefinitionStorage(Map<String, RouteDefinition> storage) {
        routeDefinitionStorage = storage;
    }

    public static RouteDefinition getRouteDefinitionBySourceEndpoint(String sourceEndpoint) {
        RouteDefinition result = routeDefinitionStorage.getOrDefault(sourceEndpoint, null);
        return result;
    }

}
