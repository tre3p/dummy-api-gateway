package unit.validator;

import com.treep.config.model.GatewayConfig;
import com.treep.exception.GatewayConfigValidationException;
import com.treep.validator.GatewayConfigValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.treep.util.constants.ConfigConstants.DEFAULT_GATEWAY_CONFIG_LOCATION;
import static com.treep.util.constants.ConfigConstants.DEFAULT_SERVER_PORT;
import static com.treep.util.constants.ConfigConstants.DEFAULT_SERVER_THREAD_COUNT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GatewayConfigValidatorTest {

    private GatewayConfig mockGatewayConfig;

    private GatewayConfigValidator gatewayConfigValidator;

    @BeforeEach
    void beforeEach() {
        mockGatewayConfig = new GatewayConfig(
                DEFAULT_SERVER_PORT,
                DEFAULT_GATEWAY_CONFIG_LOCATION,
                DEFAULT_SERVER_THREAD_COUNT
        );

        gatewayConfigValidator = new GatewayConfigValidator();
    }

    @Test
    void shouldNotThrowOnValidGatewayConfig() {
        assertDoesNotThrow(
                () -> gatewayConfigValidator.validateGatewayConfig(mockGatewayConfig)
        );
    }

    @Test
    void shouldThrowOnNonNumericPort() {
        mockGatewayConfig.setServerPort("foobarport");

        assertThrows(
                GatewayConfigValidationException.class,
                () -> gatewayConfigValidator.validateGatewayConfig(mockGatewayConfig)
        );
    }

    @Test
    void shouldThrowOnNonNumericThreadCount() {
        mockGatewayConfig.setServerThreadCount("foobarthreadcount");

        assertThrows(
                GatewayConfigValidationException.class,
                () -> gatewayConfigValidator.validateGatewayConfig(mockGatewayConfig)
        );
    }

    @Test
    void shouldThrowOnNotExistsConfigPath() {
        mockGatewayConfig.setConfigLocation("idk/what/is/this/location");

        assertThrows(
                GatewayConfigValidationException.class,
                () -> gatewayConfigValidator.validateGatewayConfig(mockGatewayConfig)
        );
    }

    @Test
    void shouldThrowOnConfigPathIsNotAFile() {
        mockGatewayConfig.setConfigLocation("src/test/resources/fixtures");

        assertThrows(
                GatewayConfigValidationException.class,
                () -> gatewayConfigValidator.validateGatewayConfig(mockGatewayConfig)
        );
    }
}
