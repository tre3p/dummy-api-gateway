package com.treep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.treep.config.GatewayConfigReader;
import com.treep.config.RoutesConfigReader;
import com.treep.config.model.GatewayConfig;
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

    public static void main(String[] args)
            throws ConfigurationReadingException, RoutesValidationException, IOException
    {
        GatewayConfig gwConfig = GatewayConfigReader.readEnv();

        ObjectMapper yamlFormattedObjectMapper = new ObjectMapper(new YAMLFactory());
        Routes configRoutes = readRoutesFromConfigFile(gwConfig.getConfigLocation(), yamlFormattedObjectMapper);
        RoutesValidator validator = new RoutesValidator();
        validateRoutes(configRoutes, validator);
        initStorage(configRoutes);

        ApiGatewayHttpHandler gatewayHttpHandler = new ApiGatewayHttpHandler();
        launchServer(gwConfig.getServerPort(), gatewayHttpHandler);
    }

    private static Routes readRoutesFromConfigFile(String configFilePath, ObjectMapper fileFormatter) throws ConfigurationReadingException {
        log.debug("+readRoutesFromConfigFile(): configFilePath: {}", configFilePath);
        RoutesConfigReader configReader = new RoutesConfigReader(fileFormatter);
        Routes readRoutes = configReader.readRouteProperties(configFilePath);
        log.debug("-readRoutesFromConfigFile()");
        return readRoutes;
    }

    private static void validateRoutes(Routes routesToValidate, RoutesValidator validator) throws RoutesValidationException {
        log.debug("+validateRoutes()");
        validator.validateRoutes(routesToValidate);
        log.debug("-validateRoutes()");
    }

    private static void initStorage(Routes storageRoutes) {
        log.info("+initStorage()");

        Map<String, RouteDefinition> storageReadyRoutes = RoutesConverter.extractSourceEndpointsToMap(storageRoutes);
        RouteDefinitionStorage.setRouteDefinitionStorage(storageReadyRoutes);

        log.info("-initStorage(): storage successfully initiated");
    }

    private static void launchServer(int port, HttpHandler rootHttpHandler) throws IOException {
        log.info("+launchServer(): launching server..");
        HttpServer s = HttpServer.create(new InetSocketAddress(port), 0);
        s.createContext("/", rootHttpHandler);
        s.start();
        log.info("-launchServer(): server successfully initialized at port: {}", port);
    }
}

