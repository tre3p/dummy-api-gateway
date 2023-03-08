package com.treep.validator;

import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.RoutesValidationException;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.treep.util.constants.ErrorConstants.NULL_FIELD_ERROR_TEMPLATE;
import static com.treep.util.constants.ErrorConstants.REQUEST_TIMEOUT_ERROR;
import static com.treep.util.constants.ErrorConstants.SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE;
import static com.treep.util.constants.ErrorConstants.SOURCE_ENDPOINT_FIELD_NAME;
import static com.treep.util.constants.ErrorConstants.SOURCE_ENDPOINT_NOT_STARTS_WITH_SLASH_ERROR;
import static com.treep.util.constants.ErrorConstants.TARGET_URL_FIELD_NAME;
import static com.treep.util.constants.ErrorConstants.URL_NOT_VALID_ERROR_TEMPLATE;

@Slf4j
public class RoutesValidator {

    private static String errorMessage;

    public static void validateRoutes(Routes routes) throws RoutesValidationException {
        log.debug("+validateRoutes(): validation {} routes", routes.getRoutes().size());
        Predicate<RouteDefinition> predicateChain = buildPredicateChain();

        for (RouteDefinition rd : routes.getRoutes()) {
            if (!predicateChain.test(rd)) {
                throw new RoutesValidationException(errorMessage);
            }
        }
        log.debug("-validateRoutes()");
    }

    private static Predicate<RouteDefinition> buildPredicateChain() {
        log.debug("buildPredicateChain()");
        return isMinOccurredFieldsNotNullOrEmpty
                .and(isSourceEndpointValid)
                .and(isSourceEndpointUnique)
                .and(isTargetUrlValid)
                .and(isRequestTimeoutValid);
    }

    /**
     * Checks that all necessary fields in {@link Routes} are not null and not empty.
     */
    private static final Predicate<RouteDefinition> isMinOccurredFieldsNotNullOrEmpty = new Predicate<>() {
        @Override
        public boolean test(RouteDefinition r) {
            log.debug("+isMinOccurredFieldsNotNullOrEmpty()");
            String sourceEndpoint = r.getSourceEndpoint();
            String targetUrl = r.getTargetUrl();
            boolean result = true;

            if (sourceEndpoint == null || sourceEndpoint.isEmpty()) {
                errorMessage = String.format(NULL_FIELD_ERROR_TEMPLATE, SOURCE_ENDPOINT_FIELD_NAME);
                result = false;
            } else if (targetUrl == null || targetUrl.isEmpty()) {
                errorMessage = String.format(NULL_FIELD_ERROR_TEMPLATE, TARGET_URL_FIELD_NAME);
                result = false;
            }

            log.debug("-isMinOccurredFieldsNotNullOrEmpty(): result: {}", result);
            return result;
        }
    };

    /**
     * Checks if {@link Routes} contains non-unique 'source-endpoint' field
     */
    private static final Predicate<RouteDefinition> isSourceEndpointUnique = new Predicate<>() {

        private final Set<String> seenSourceEndpoints = new HashSet<>();

        @Override
        public boolean test(RouteDefinition r) {
            log.debug("+isSourceEndpointUnique()");
            String sourceEndpoint = r.getSourceEndpoint();
            boolean isSeenHashContains = seenSourceEndpoints.contains(sourceEndpoint);
            boolean result = true;

            if (isSeenHashContains) {
                result = false;
                errorMessage = String.format(SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE, sourceEndpoint);
            }

            seenSourceEndpoints.add(sourceEndpoint);
            log.debug("-isSourceEndpointUnique(): result: {}", result);
            return result;
        }
    };

    /**
     * Checks that URL passed in {@link Routes} is valid URL
     */
    private static final Predicate<RouteDefinition> isTargetUrlValid = new Predicate<>() {
        @Override
        public boolean test(RouteDefinition r) {
            log.debug("+isTargetUrlValid()");
            String url = r.getTargetUrl();
            boolean result = true;

            try {
                new URL(url).toURI();
            } catch (MalformedURLException | URISyntaxException e) {
                errorMessage = String.format(URL_NOT_VALID_ERROR_TEMPLATE, url);
                result = false;
            }

            log.debug("-isTargetUrlValid(): result: {}", result);
            return result;
        }
    };

    /**
     * Checks that request timeout is not negative
     */
    private static final Predicate<RouteDefinition> isRequestTimeoutValid = new Predicate<>() {
        @Override
        public boolean test(RouteDefinition r) {
            log.debug("+isRequestTimeoutValid()");
            boolean result = true;

            if (r.getRequestTimeout() < 0) {
                errorMessage = REQUEST_TIMEOUT_ERROR;
                result = false;
            }

            log.debug("-isRequestTimeoutValid(): result: {}", result);
            return result;
        }
    };

    private static final Predicate<RouteDefinition> isSourceEndpointValid = routeDefinition -> {
        log.debug("+isSourceEndpointValid()");
        String sourceEndpoint = routeDefinition.getSourceEndpoint();
        boolean result = true;

        if (!sourceEndpoint.startsWith("/")) {
            errorMessage = String.format(SOURCE_ENDPOINT_NOT_STARTS_WITH_SLASH_ERROR, sourceEndpoint);
            result = false;
        }

        log.debug("-isSourceEndpointValid(): result: {}", result);
        return result;
    };
}
