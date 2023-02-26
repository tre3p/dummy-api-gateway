package com.treep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.ConfigurationReadingException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoutesConfigReader {

    private static final String ERROR_CONFIG_READING = "Error while reading configuration.";

    private static final String ERROR_CONFIG_INVALID = "Configuration is invalid, 'source-endpoint' must be unique.";

    private final ObjectMapper objectMapper;

    public RoutesConfigReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Reads file in any format in which {@link ObjectMapper} configured.
     * Creates Map<String, RouteDefinition>, where key is 'source-endpoint' field from configuration file.
     *
     * @param routesFilePath - path to file with configurations
     * @return
     */
    public Map<String, RouteDefinition> readRouteProperties(String routesFilePath)
            throws ConfigurationReadingException {
        File routesConfigFile = new File(routesFilePath);

        Routes gatewayRoutes;

        try {
            gatewayRoutes = objectMapper.readValue(routesConfigFile, Routes.class);
        } catch (IOException e) {
            throw new ConfigurationReadingException(ERROR_CONFIG_READING, e);
        }

        if (!isSourceEndpointsUnique(gatewayRoutes)) {
            throw new ConfigurationReadingException(ERROR_CONFIG_INVALID);
        }

        Map<String, RouteDefinition> result = extractSourceEndpointsToMap(gatewayRoutes);

        return result;
    }

    private boolean isSourceEndpointsUnique(Routes gatewayRoutes) {
        Set<String> seenEndpoints = new HashSet<>();

        for (RouteDefinition d : gatewayRoutes.getRoutes()) {
            if (seenEndpoints.contains(d.getSourceEndpoint())) {
                return false;
            }
            seenEndpoints.add(d.getSourceEndpoint());
        }

        return true;
    }

    private Map<String, RouteDefinition> extractSourceEndpointsToMap(Routes gatewayRoutes) {
        Map<String, RouteDefinition> result = new HashMap<>();

        gatewayRoutes.getRoutes().forEach(r -> {
                    String sourceEndpoint = r.getSourceEndpoint();
                    result.put(sourceEndpoint, r);
                });

        return result;
    }

}
