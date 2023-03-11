package com.treep.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class EnvUtils {

    private EnvUtils() { }

    public static String getEnv(String envName) {
        log.debug("+getEnv(): envName: {}", envName);
        String envValue = System.getenv(envName);
        log.debug("-getEnv(): envValue: {}", envValue);
        return envValue;
    }
}
