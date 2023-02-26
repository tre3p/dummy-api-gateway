package com.treep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treep.config.model.Routes;
import com.treep.exception.ConfigurationReadingException;
import lombok.extern.slf4j.Slf4j;
import java.io.File;

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
    public Routes readRouteProperties(String routesFilePath)
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

        return gatewayRoutes;
    }

}
