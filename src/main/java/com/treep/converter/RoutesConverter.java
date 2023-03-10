package com.treep.converter;

import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class RoutesConverter {

    private RoutesConverter() { }

    /**
     * Creates Map<String, RouteDefinition> from {@link Routes} passed in method, where key is 'source-endpoint',
     * and value is {@link RouteDefinition} associated with this 'source-endpoint'
     * @param gatewayRoutes constructed {@link Routes}
     * @return constructed Map<String, RouteDefinition>
     */
    public static Map<String, RouteDefinition> extractSourceEndpointsToMap(Routes gatewayRoutes) {
        log.debug("+extractSourceEndpointsToMap(): extracting source endpoints to map");
        Map<String, RouteDefinition> result = new HashMap<>();

        gatewayRoutes.getRoutes().forEach(r -> {
            String sourceEndpoint = r.getSourceEndpoint();
            result.put(sourceEndpoint, r);
        });

        log.debug("-extractSourceEndpointsToMap(): endpoints extracted. total map size: {}", result.size());
        return result;
    }

}
