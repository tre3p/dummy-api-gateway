package validator;

import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.RoutesValidationException;
import com.treep.validator.RoutesValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.treep.util.ErrorConstants.NULL_FIELD_ERROR_TEMPLATE;
import static com.treep.util.ErrorConstants.REQUEST_TIMEOUT_ERROR;
import static com.treep.util.ErrorConstants.SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE;
import static com.treep.util.ErrorConstants.SOURCE_ENDPOINT_FIELD_NAME;
import static com.treep.util.ErrorConstants.SOURCE_ENDPOINT_NOT_STARTS_WITH_SLASH_ERROR;
import static com.treep.util.ErrorConstants.TARGET_URL_FIELD_NAME;
import static com.treep.util.ErrorConstants.URL_NOT_VALID_ERROR_TEMPLATE;

public class RoutesValidatorTest {

    private Routes testRoutes;

    private RoutesValidator routesValidator;

    @BeforeEach
    void beforeEach() {
        testRoutes = new Routes(
                List.of(
                        new RouteDefinition(
                                "http://localhost",
                                "/testendpoint",
                                10
                        )
                )
        );

        routesValidator = new RoutesValidator();
    }

    @Test
    void shouldThrowAndReturnCorrectMessageOnNullSourceEndpoint() {
        testRoutes.getRoutes().get(0).setSourceEndpoint(null);
        String expectedMessage = String.format(
                NULL_FIELD_ERROR_TEMPLATE,
                SOURCE_ENDPOINT_FIELD_NAME
        );

        var actualExceptionMessage = Assertions.assertThrowsExactly(
                RoutesValidationException.class,
                () -> routesValidator.validateRoutes(testRoutes)
        ).getMessage();
        Assertions.assertEquals(expectedMessage, actualExceptionMessage);
    }

    @Test
    void shouldThrowAndReturnCorrectMessageOnNullTargetUrl() {
        testRoutes.getRoutes().get(0).setTargetUrl(null);
        String expectedMessage = String.format(
                NULL_FIELD_ERROR_TEMPLATE,
                TARGET_URL_FIELD_NAME
        );

        var actualExceptionMessage = Assertions.assertThrows(
                RoutesValidationException.class,
                () -> routesValidator.validateRoutes(testRoutes)
        ).getMessage();

        Assertions.assertEquals(expectedMessage, actualExceptionMessage);
    }

    @Test
    void shouldThrowAndReturnCorrectMessageOnNonValidTargetUrl() {
        testRoutes.getRoutes().get(0).setTargetUrl("non-correct");
        String expectedMessage = String.format(
                URL_NOT_VALID_ERROR_TEMPLATE,
                testRoutes.getRoutes().get(0).getTargetUrl()
        );

        var actualExceptionMessage = Assertions.assertThrows(
                RoutesValidationException.class,
                () -> routesValidator.validateRoutes(testRoutes)
        ).getMessage();

        Assertions.assertEquals(expectedMessage, actualExceptionMessage);
    }

    @Test
    void shouldThrowAndReturnCorrectMessageOnNegativeRequestTimeout() {
        testRoutes.getRoutes().get(0).setRequestTimeout(-2);

        var actualExceptionMessage = Assertions.assertThrows(
                RoutesValidationException.class,
                () -> routesValidator.validateRoutes(testRoutes)
        ).getMessage();

        Assertions.assertEquals(REQUEST_TIMEOUT_ERROR, actualExceptionMessage);
    }

    @Test
    void shouldThrowAndReturnCorrectMessageOnIncorrectSourceEndpoint() {
        testRoutes.getRoutes().get(0).setSourceEndpoint("without-slash");
        String expectedMessage = String.format(
                SOURCE_ENDPOINT_NOT_STARTS_WITH_SLASH_ERROR,
                testRoutes.getRoutes().get(0).getSourceEndpoint()
        );

        var actualExceptionMessage = Assertions.assertThrows(
                RoutesValidationException.class,
                () -> routesValidator.validateRoutes(testRoutes)
        ).getMessage();

        Assertions.assertEquals(expectedMessage, actualExceptionMessage);
    }

    @Test
    void shouldThrowAndReturnCorrectMessageOnSourceEndpointsDuplicate() {
        var routeDefinition = new RouteDefinition(
                "http://localhost",
                "/source",
                10
        );

        testRoutes = new Routes(
                List.of(
                        routeDefinition,
                        routeDefinition
                )
        );

        String expectedMessage = String.format(
                SOURCE_ENDPOINTS_NOT_UNIQUE_ERROR_TEMPLATE,
                routeDefinition.getSourceEndpoint()
        );

        var actualExceptionMessage = Assertions.assertThrows(
                RoutesValidationException.class,
                () -> routesValidator.validateRoutes(testRoutes)
        ).getMessage();

        Assertions.assertEquals(expectedMessage, actualExceptionMessage);
    }

    @Test
    void shouldNotThrowOnCorrectConfig() {
        Assertions.assertDoesNotThrow(
                () -> routesValidator.validateRoutes(testRoutes)
        );
    }
}
