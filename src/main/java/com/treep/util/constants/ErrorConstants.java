package com.treep.util.constants;

public class ErrorConstants {

    public static final String SOURCE_ENDPOINT_FIELD_NAME = "source-endpoint";

    public static final String TARGET_URL_FIELD_NAME = "target-url";

    public static final String NULL_FIELD_ERROR_TEMPLATE = "Field '%s' can't be null or empty.";

    public static final String SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE =
            "Source endpoints are not unique at source-endpoint '%s'.";

    public static final String SOURCE_ENDPOINT_NOT_STARTS_WITH_SLASH_ERROR =
            "Source endpoint at '%s' does not starts with slash.";

    public static final String URL_NOT_VALID_ERROR_TEMPLATE = "URL not valid at target-url '%s'.";

    public static final String REQUEST_TIMEOUT_ERROR = "Request timeout can't be less then zero.";

    public static final String CONFIG_READING_ERROR = "Error while reading configuration.";

    public static final String SOURCE_ENDPOINT_NOT_FOUND_ERROR_TEMPLATE =
            "Source endpoint at '%s' not found.";

    public static final String ERROR_BUILDING_REQUEST_MODEL = "Error while building request model. %s";

    public static final String ERROR_EXECUTING_REQUEST = "Error while executing request. Cause: %s";
}
