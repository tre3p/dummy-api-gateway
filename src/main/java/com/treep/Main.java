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
import com.treep.exception.GatewayConfigValidationException;
import com.treep.exception.RoutesValidationException;
import com.treep.executor.RequestExecutor;
import com.treep.handler.ApiGatewayHttpHandler;
import com.treep.storage.RouteDefinitionStorage;
import com.treep.validator.GatewayConfigValidator;
import com.treep.validator.RoutesValidator;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.Executors;

@Slf4j
public class Main {

    public static void main(String[] args) throws
            ConfigurationReadingException,
            RoutesValidationException,
            IOException,
            GatewayConfigValidationException
    {
        log.debug("+main()");
        var gwConfig = GatewayConfigReader.readEnv();
        validateGatewayConfig(gwConfig);

        var validRoutes = prepareRoutes(gwConfig.getConfigLocation());
        initStorage(validRoutes);

        var gatewayHttpHandler = prepareHttpHandler();
        launchServer(
                Integer.parseInt(gwConfig.getServerPort()),
                Integer.parseInt(gwConfig.getServerThreadCount()),
                gatewayHttpHandler);
        log.debug("-main()");
    }

    private static void validateGatewayConfig(GatewayConfig gatewayConfig) throws GatewayConfigValidationException {
        log.debug("+validateGatewayConfig()");
        GatewayConfigValidator validator = new GatewayConfigValidator();
        validator.validateGatewayConfig(gatewayConfig);
        log.debug("-validateGatewayConfig()");
    }

    private static Routes prepareRoutes(String configLocation)
            throws ConfigurationReadingException, RoutesValidationException
    {
        log.debug("+prepareRoutes()");
        ObjectMapper yamlFormattedObjectMapper = new ObjectMapper(new YAMLFactory());
        Routes configRoutes = readRoutesFromConfigFile(configLocation, yamlFormattedObjectMapper);
        RoutesValidator validator = new RoutesValidator();
        validateRoutes(configRoutes, validator);

        log.debug("-prepareRoutes()");
        return configRoutes;
    }

    private static Routes readRoutesFromConfigFile(String configFilePath, ObjectMapper fileFormatter)
            throws ConfigurationReadingException
    {
        log.debug("+readRoutesFromConfigFile(): configFilePath: {}", configFilePath);
        RoutesConfigReader configReader = new RoutesConfigReader(fileFormatter);
        Routes readRoutes = configReader.readRouteProperties(configFilePath);
        log.debug("-readRoutesFromConfigFile()");
        return readRoutes;
    }

    private static void validateRoutes(Routes routesToValidate, RoutesValidator validator)
            throws RoutesValidationException
    {
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

    private static ApiGatewayHttpHandler prepareHttpHandler() {
        log.debug("+prepareHttpHandler()");
        HttpClient httpClient = buildDefaultHttpClient();

        ObjectMapper jsonFormattedObjectMapper = new ObjectMapper();
        RequestExecutor requestExecutor = new RequestExecutor(httpClient);
        ApiGatewayHttpHandler gatewayHttpHandler = new ApiGatewayHttpHandler(
                jsonFormattedObjectMapper,
                requestExecutor
        );

        log.debug("-prepareHttpHandler()");
        return gatewayHttpHandler;
    }

    private static HttpClient buildDefaultHttpClient() {
        log.debug("buildDefaultHttpClient()");
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    private static void launchServer(int port, int serverThreadCount, HttpHandler rootHttpHandler) throws IOException {
        log.info("+launchServer(): launching server..");
        HttpServer s = HttpServer.create(new InetSocketAddress(port), 0);
        s.createContext("/", rootHttpHandler);
        s.setExecutor(Executors.newFixedThreadPool(serverThreadCount));
        s.start();
        log.info("-launchServer(): server successfully initialized at port: {}", port);
    }
}

