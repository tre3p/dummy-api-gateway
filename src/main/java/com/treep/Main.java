package com.treep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.net.httpserver.HttpServer;
import com.treep.config.RoutesConfigReader;
import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.converter.RoutesConverter;
import com.treep.exception.ConfigurationReadingException;
import com.treep.exception.RoutesValidationException;
import com.treep.handler.ApiGatewayHttpHandler;
import com.treep.storage.RouteDefinitionStorage;
import com.treep.validator.RoutesValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

@Slf4j
public class Main {

    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/gateway-config.yml";
    private static final int DEFAULT_SERVER_PORT = 8080;

    public static void main(String[] args)
            throws ConfigurationReadingException, RoutesValidationException, IOException
    {
        initStorage(DEFAULT_CONFIG_PATH);
        launchServer(DEFAULT_SERVER_PORT);
    }

    private static void initStorage(String configPath) throws ConfigurationReadingException, RoutesValidationException {
        log.info("+initStorage()");
        ObjectMapper yamlReaderObjectMapper = new ObjectMapper(new YAMLFactory());
        RoutesConfigReader configReader = new RoutesConfigReader(yamlReaderObjectMapper);
        RoutesValidator routesValidator = new RoutesValidator();

        log.debug("initStorage(): reading routes from path: {}", configPath);
        Routes readedRoutes = configReader.readRouteProperties(configPath);
        log.debug("initStorage(): routes successfully read");

        log.debug("initStorage(): validating routes");
        routesValidator.validateRoutes(readedRoutes);
        log.debug("initStorage(): routes successfully validated");

        Map<String, RouteDefinition> storageReadyRoutes = RoutesConverter.extractSourceEndpointsToMap(readedRoutes);
        RouteDefinitionStorage.setRouteDefinitionStorage(storageReadyRoutes);
        log.info("-initStorage(): storage successfully initiated");
    }

    private static void launchServer(int port) throws IOException {
        log.info("+launchServer(): launching server..");
        HttpServer s = HttpServer.create(new InetSocketAddress(port), 0);
        s.createContext("/", new ApiGatewayHttpHandler());
        s.start();
        log.info("-launchServer(): server successfully initialized at port: {}", port);
    }
}

