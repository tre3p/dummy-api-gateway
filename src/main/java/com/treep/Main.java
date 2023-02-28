package com.treep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.treep.config.RoutesConfigReader;
import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.convertor.RoutesConvertor;
import com.treep.exception.ConfigurationReadingException;
import com.treep.exception.RoutesValidationException;
import com.treep.handler.ApiGatewayHttpHandler;
import com.treep.storage.RouteDefinitionStorage;
import com.treep.validator.RoutesValidator;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Main {

    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/gateway-config.yml";
    private static final int DEFAULT_SERVER_PORT = 8080;

    public static void main(String[] args) throws ConfigurationReadingException, RoutesValidationException {
        initStorage(DEFAULT_CONFIG_PATH);
        launchServer(new ApiGatewayHttpHandler(), DEFAULT_SERVER_PORT);
    }

    private static void initStorage(String configPath) throws ConfigurationReadingException, RoutesValidationException {
        log.debug("+initStorage()");
        ObjectMapper yamlReaderObjectMapper = new ObjectMapper(new YAMLFactory());
        RoutesConfigReader configReader = new RoutesConfigReader(yamlReaderObjectMapper);
        RoutesValidator routesValidator = new RoutesValidator();

        log.info("initStorage(): reading routes from path: {}", configPath);
        Routes readedRoutes = configReader.readRouteProperties(configPath);
        log.info("initStorage(): routes successfully read");

        log.info("initStorage(): validating routes");
        routesValidator.validateRoutes(readedRoutes);
        log.info("initStorage(): routes successfully validated");

        Map<String, RouteDefinition> storageReadyRoutes = RoutesConvertor.extractSourceEndpointsToMap(readedRoutes);
        RouteDefinitionStorage.setRouteDefinitionStorage(storageReadyRoutes);
        log.debug("-initStorage(): storage successfully initiated");
    }

    private static void launchServer(HttpHandler gatewayHandler, int port) {
        log.debug("+launchServer(): launching server at port: {}", port);
        Undertow s = Undertow.builder()
                .addHttpListener(port, "0.0.0")
                .setHandler(new BlockingHandler(gatewayHandler))
                .build();

        s.start();
        log.debug("-launchServer(): server successfully launched");
    }
}

