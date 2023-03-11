package com.treep.config;

import com.treep.config.model.GatewayConfig;
import com.treep.util.EnvUtils;
import lombok.extern.slf4j.Slf4j;

import static com.treep.util.constants.ConfigConstants.DEFAULT_GATEWAY_CONFIG_LOCATION;
import static com.treep.util.constants.ConfigConstants.DEFAULT_SERVER_PORT;
import static com.treep.util.constants.ConfigConstants.DEFAULT_SERVER_THREAD_COUNT;
import static com.treep.util.constants.ConfigConstants.GATEWAY_CONFIG_LOCATION_ENV;
import static com.treep.util.constants.ConfigConstants.SERVER_PORT_ENV;
import static com.treep.util.constants.ConfigConstants.SERVER_THREAD_COUNT;

@Slf4j
public final class GatewayConfigReader {

    private GatewayConfigReader() { }

    public static GatewayConfig readEnv() {
        log.debug("+readEnv()");
        String gatewayConfigLocation = readConfigEnv(GATEWAY_CONFIG_LOCATION_ENV, DEFAULT_GATEWAY_CONFIG_LOCATION);
        String serverPort = readConfigEnv(SERVER_PORT_ENV, DEFAULT_SERVER_PORT);
        String serverThreadCount = readConfigEnv(SERVER_THREAD_COUNT, DEFAULT_SERVER_THREAD_COUNT);

        GatewayConfig gwConfig = new GatewayConfig(
                serverPort,
                gatewayConfigLocation,
                serverThreadCount
        );

        log.debug("-readEnv(): gwConfig: {}", gwConfig);
        return gwConfig;
    }

    private static String readConfigEnv(String envName, String defaultValue) {
        log.debug("+readConfigEnv(): envName: {}", envName);

        String envValue = EnvUtils.getEnv(envName);

        if (envValue == null) {
            log.debug("-readConfigEnv(): defaultValue: {}", defaultValue);
            return defaultValue;
        }

        log.debug("-readConfigValue(): envValue: {}", envValue);
        return envValue;
    }


}
