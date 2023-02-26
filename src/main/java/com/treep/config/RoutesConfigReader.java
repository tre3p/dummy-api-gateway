package com.treep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.ConfigurationReadingException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.treep.util.Constants.ERROR_CONFIG_INVALID;
import static com.treep.util.Constants.ERROR_CONFIG_READING;

@Slf4j
public class RoutesConfigReader {

    /**
     * ObjectMapper implementation for file reading.
     */
    private final ObjectMapper objectMapper;

    public RoutesConfigReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Reads file in any format in which {@link ObjectMapper} configured.
     * Creates Map<String, RouteDefinition>, where key is 'source-endpoint' field from configuration file.
     *
     * @param routesFilePath - path to file with configurations
     * @return constructed Map<String, RouteDefinition>
     */
    public Map<String, RouteDefinition> readRouteProperties(String routesFilePath)
            throws ConfigurationReadingException {
        log.debug("+readRouteProperties(): reading properties from file: {}", routesFilePath);
        File routesConfigFile;
        Routes gatewayRoutes;

        try {
            routesConfigFile = new File(routesFilePath);
            gatewayRoutes = objectMapper.readValue(routesConfigFile, Routes.class);
        } catch (Exception e) {
            log.error("-readRouteProperties(): error while reading file ", e);
            throw new ConfigurationReadingException(ERROR_CONFIG_READING, e);
        }

        if (!isSourceEndpointsUnique(gatewayRoutes)) {
            log.error("-readRouteProperties(): 'source-endpoint' are not unique.");
            throw new ConfigurationReadingException(ERROR_CONFIG_INVALID);
        }

        Map<String, RouteDefinition> result = extractSourceEndpointsToMap(gatewayRoutes);
        log.debug("-readRouteProperties(): properties successfully read. endpoints size: {}", result.size());
        return result;
    }

    /**
     * Checks if {@link Routes} contains non-unique 'source-endpoint' field
     * @param gatewayRoutes constructed {@link Routes} objects with routes
     * @return 'false' in case of 'source-endpoint' is not unique
     */
    private boolean isSourceEndpointsUnique(Routes gatewayRoutes) {
        log.debug("+isSourceEndpointsUnique()");
        Set<String> seenEndpoints = new HashSet<>();

        for (RouteDefinition d : gatewayRoutes.getRoutes()) {
            if (seenEndpoints.contains(d.getSourceEndpoint())) {
                return false;
            }
            seenEndpoints.add(d.getSourceEndpoint());
        }

        log.debug("-isSourceEndpointsUnique()");
        return true;
    }

    /**
     * Creates Map<String, RouteDefinition> from {@link Routes} passed in method, where key is 'source-endpoint',
     * and value is {@link RouteDefinition} associated with this 'source-endpoint'
     * @param gatewayRoutes constructed {@link Routes}
     * @return constructed Map<String, RouteDefinition>
     */
    private Map<String, RouteDefinition> extractSourceEndpointsToMap(Routes gatewayRoutes) {
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
