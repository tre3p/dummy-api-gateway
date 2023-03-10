package com.treep.util;

import lombok.extern.slf4j.Slf4j;

import static com.treep.util.constants.ConfigConstants.GATEWAY_CONFIG_LOCATION_ENV;
import static com.treep.util.constants.ConfigConstants.SERVER_PORT_ENV;
import static com.treep.util.constants.ConfigConstants.SERVER_PORT_ENV_NOT_FOUND_VALUE;

@Slf4j
public final class EnvUtils {

    private EnvUtils() { }

    public static String getGatewayConfigLocation() {
        log.debug("+getGatewayConfigLocation()");
        String gatewayConfigLocation = System.getenv(GATEWAY_CONFIG_LOCATION_ENV);
        log.debug("-getGatewayConfigLocation(): getGatewayConfigLocation: {} ", gatewayConfigLocation);
        return gatewayConfigLocation;
    }

    public static int getServerPort() {
        log.debug("+getServerPort()");
        String serverPortStr = System.getenv(SERVER_PORT_ENV);

        if (serverPortStr == null) {
            return SERVER_PORT_ENV_NOT_FOUND_VALUE;
        }

        int serverPort = Integer.parseInt(serverPortStr);
        log.debug("-getServerPort(): serverPort: {}", serverPort);
        return serverPort;
    }
}
