package com.treep.config;

import com.treep.config.model.GatewayConfig;
import com.treep.util.EnvUtils;
import lombok.extern.slf4j.Slf4j;

import static com.treep.util.constants.ConfigConstants.DEFAULT_GATEWAY_CONFIG_LOCATION;
import static com.treep.util.constants.ConfigConstants.DEFAULT_SERVER_PORT;
import static com.treep.util.constants.ConfigConstants.SERVER_PORT_ENV_NOT_FOUND_VALUE;

@Slf4j
public class GatewayConfigReader {

    public static GatewayConfig readEnv() {
        log.debug("+readEnv()");
        String gatewayConfigLocation = readGatewayConfigLocation();
        int serverPort = readServerPortEnv();

        GatewayConfig gwConfig = new GatewayConfig(
                serverPort,
                gatewayConfigLocation
        );

        log.debug("-readEnv(): gwConfig: {}", gwConfig);
        return gwConfig;
    }

    private static int readServerPortEnv() {
        log.debug("+readServerPortEnv()");
        int serverPort = EnvUtils.getServerPort();

        serverPort = serverPort == SERVER_PORT_ENV_NOT_FOUND_VALUE ? DEFAULT_SERVER_PORT : serverPort;
        log.debug("-readServerPortEnv(): serverPort: {}", serverPort);
        return serverPort;
    }

    private static String readGatewayConfigLocation() {
        log.debug("+getGatewayConfigLocation():");
        String gatewayConfigLocation = EnvUtils.getGatewayConfigLocation();

        gatewayConfigLocation = gatewayConfigLocation == null ? DEFAULT_GATEWAY_CONFIG_LOCATION : gatewayConfigLocation;
        log.debug("-getGatewayConfigLocation(): gatewayConfigLocation: {}", gatewayConfigLocation);
        return gatewayConfigLocation;
    }

}
