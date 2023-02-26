package com.treep.validator;

import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

import static com.treep.util.Constants.NULL_FIELD_TEMPLATE;
import static com.treep.util.Constants.SOURCE_ENDPOINT_FIELD_NAME;
import static com.treep.util.Constants.TARGET_URL_FIELD_NAME;

@Slf4j
public class RoutesValidator {

    private static String errorMessage;

    public static boolean isRoutesValid(Routes gatewayRoutes) {
        return gatewayRoutes.getRoutes().stream()
                .anyMatch(RoutesValidator::isValid);
    }

    private static boolean isValid(RouteDefinition routeDefinition) {
        String sourceEndpoint = routeDefinition.getSourceEndpoint();
        String targetUrl = routeDefinition.getTargetUrl();

        if (sourceEndpoint == null || sourceEndpoint.isEmpty()) {
            errorMessage = String.format(NULL_FIELD_TEMPLATE, SOURCE_ENDPOINT_FIELD_NAME);
        } else if (targetUrl == null || targetUrl.isEmpty()) {
            errorMessage = String.format(NULL_FIELD_TEMPLATE, TARGET_URL_FIELD_NAME);
        }

        return sourceEndpoint != null && targetUrl != null;
    }

    /**
     * Checks if {@link Routes} contains non-unique 'source-endpoint' field
     * @param gatewayRoutes constructed {@link Routes} objects with routes
     * @return 'false' in case of 'source-endpoint' is not unique
     */
    public static boolean isSourceEndpointsUnique(Routes gatewayRoutes) {
        log.debug("+isSourceEndpointsUnique()");
        Set<String> seenEndpoints = new HashSet<>();

        for (RouteDefinition d : gatewayRoutes.getRoutes()) {
            if (seenEndpoints.contains(d.getSourceEndpoint())) {
                return false;
            }
            seenEndpoints.add(d.getSourceEndpoint());
        }

        log.debug("-isSourceEndpointsUnique()");
        return true;
    }

    public static String getErrorMessage() {
        return errorMessage;
    }
}
