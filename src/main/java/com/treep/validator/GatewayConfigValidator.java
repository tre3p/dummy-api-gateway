package com.treep.validator;

import com.treep.config.model.GatewayConfig;
import com.treep.exception.GatewayConfigValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.function.Predicate;

import static com.treep.util.constants.ErrorConstants.ROUTE_PATH_CANT_BE_FOUND;
import static com.treep.util.constants.ErrorConstants.VALUE_IS_NOT_NUMERIC;

@Slf4j
public class GatewayConfigValidator {

    private String errorMessage;

    public void validateGatewayConfig(GatewayConfig gatewayConfig) throws GatewayConfigValidationException {
        log.debug("+validateGatewayConfig()");
        Predicate<GatewayConfig> predicateChain = buildPredicateChain();

        if (!predicateChain.test(gatewayConfig)) {
            throw new GatewayConfigValidationException(errorMessage);
        }
        log.debug("-validateGatewayConfig()");
    }

    private Predicate<GatewayConfig> buildPredicateChain() {
        log.debug("buildPredicateChain()");
        return validateNumericFields.and(validateConfigPath);
    }

    private Predicate<GatewayConfig> validateNumericFields = gatewayConfig -> {
        log.debug("+validateNumericFields()");
        boolean isValid = true;

        try {
            Integer.parseInt(gatewayConfig.getServerThreadCount());
            Integer.parseInt(gatewayConfig.getServerPort());
        } catch (NumberFormatException e) {
            isValid = false;
            errorMessage = String.format(VALUE_IS_NOT_NUMERIC, e.getMessage());
        }

        log.debug("-validateNumericFields(): isValid: {}", isValid);
        return isValid;
    };

    private Predicate<GatewayConfig> validateConfigPath = gatewayConfig -> {
        log.debug("+validateConfigPath()");

        boolean isValid = true;
        String configPath = gatewayConfig.getConfigLocation();

        if (!new File(configPath).isFile()) {
            isValid = false;
            errorMessage = String.format(ROUTE_PATH_CANT_BE_FOUND, configPath);
        }

        log.debug("-validateConfigPath(): isValid: {}", isValid);
        return isValid;
    };
}
