package com.treep.validator;

import com.treep.config.model.Routes;
import com.treep.exception.RoutesValidationException;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static com.treep.validator.ErrorConstants.NULL_FIELD_ERROR_TEMPLATE;
import static com.treep.validator.ErrorConstants.REQUEST_TIMEOUT_ERROR;
import static com.treep.validator.ErrorConstants.SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE;
import static com.treep.validator.ErrorConstants.SOURCE_ENDPOINT_FIELD_NAME;
import static com.treep.validator.ErrorConstants.TARGET_URL_FIELD_NAME;
import static com.treep.validator.ErrorConstants.URL_NOT_VALID_ERROR_TEMPLATE;

@Slf4j
public class RoutesValidator {

    private static String errorMessage;

    public static void validateRoutes(Routes routes) throws RoutesValidationException {
        boolean isValid = isMinOccurredFieldsNullOrEmpty(routes)
                && isSourceEndpointUnique(routes)
                && isTargetUrlValid(routes)
                && isRequestTimeoutValid(routes);

        if (!isValid) {
            throw new RoutesValidationException(errorMessage);
        }
    }

    /**
     * Checks that all necessary fields in {@link Routes} are not null and not empty.
     */
    private static boolean isMinOccurredFieldsNullOrEmpty(Routes routes) {
        return routes.getRoutes().stream().anyMatch(r -> {
            String sourceEndpoint = r.getSourceEndpoint();
            String targetUrl = r.getTargetUrl();

            if (sourceEndpoint == null || sourceEndpoint.isEmpty()) {
                errorMessage = String.format(NULL_FIELD_ERROR_TEMPLATE, SOURCE_ENDPOINT_FIELD_NAME);
                return false;
            } else if (targetUrl == null || targetUrl.isEmpty()) {
                errorMessage = String.format(NULL_FIELD_ERROR_TEMPLATE, TARGET_URL_FIELD_NAME);
                return false;
            }

            return true;
        });
    }

    /**
     * Checks if {@link Routes} contains non-unique 'source-endpoint' field
     */
    private static boolean isSourceEndpointUnique(Routes routes) {
        Set<String> seenSourceEndpoints = new HashSet<>();

        return routes.getRoutes().stream().anyMatch(r -> {
            String sourceEndpoint = r.getSourceEndpoint();

            if (seenSourceEndpoints.contains(sourceEndpoint)) {
                errorMessage = String.format(SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE, sourceEndpoint);
                return false;
            }

            seenSourceEndpoints.add(sourceEndpoint);
            return true;
        });
    }

    /**
     * Checks that URL passed in {@link Routes} is valid URL
     */
    private static boolean isTargetUrlValid(Routes routes) {
        return routes.getRoutes().stream().anyMatch(r -> {
            String url = r.getTargetUrl();

            try {
                new URL(url).toURI();
            } catch (MalformedURLException | URISyntaxException e) {
                errorMessage = String.format(URL_NOT_VALID_ERROR_TEMPLATE, url);
                return false;
            }

            return true;
        });
    }

    /**
     * Checks that request timeout is not negative
     */
    private static boolean isRequestTimeoutValid(Routes routes) {
        return routes.getRoutes().stream().anyMatch(r -> {
            if (r.getRequestTimeout() < 0) {
                errorMessage = REQUEST_TIMEOUT_ERROR;
                return false;
            }

            return true;
        });
    }
}
