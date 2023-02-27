package validator;

import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.RoutesValidationException;
import com.treep.validator.RoutesValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RoutesValidatorTest {

    private Routes testRoutes;

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
    }

    @Test
    void shouldReturnFalseOnNullSourceEndpoint() {
        testRoutes.getRoutes().get(0).setSourceEndpoint(null);

        Assertions.assertThrowsExactly(
                RoutesValidationException.class,
                () -> RoutesValidator.validateRoutes(testRoutes)
        );
    }

    @Test
    void shouldReturnFalseOnNullTargetUrl() {
        testRoutes.getRoutes().get(0).setTargetUrl(null);

        Assertions.assertThrows(
                RoutesValidationException.class,
                () -> RoutesValidator.validateRoutes(testRoutes)
        );
    }

    @Test
    void shouldThrowOnNonValidTargetUrl() {
        testRoutes.getRoutes().get(0).setTargetUrl("non-correct");

        Assertions.assertThrows(
                RoutesValidationException.class,
                () -> RoutesValidator.validateRoutes(testRoutes)
        );
    }

    @Test
    void shouldNotThrowOnCorrectTargetUrlPassed() {
        testRoutes.getRoutes().get(0).setTargetUrl("http://localhost");
        Assertions.assertDoesNotThrow(
                () -> RoutesValidator.validateRoutes(testRoutes)
        );
    }

    @Test
    void shouldThrowOnNegativeRequestTimeout() {
        testRoutes.getRoutes().get(0).setRequestTimeout(-2);

        Assertions.assertThrows(
                RoutesValidationException.class,
                () -> RoutesValidator.validateRoutes(testRoutes)
        );
    }

    @Test
    void shouldNotThrowOnCorrectConfig() {
        Assertions.assertDoesNotThrow(
                () -> RoutesValidator.validateRoutes(testRoutes)
        );
    }
}
