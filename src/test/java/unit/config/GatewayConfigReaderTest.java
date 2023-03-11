package unit.config;

import com.treep.config.GatewayConfigReader;
import com.treep.config.model.GatewayConfig;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static com.treep.util.constants.ConfigConstants.DEFAULT_GATEWAY_CONFIG_LOCATION;
import static com.treep.util.constants.ConfigConstants.DEFAULT_SERVER_PORT;
import static com.treep.util.constants.ConfigConstants.GATEWAY_CONFIG_LOCATION_ENV;
import static com.treep.util.constants.ConfigConstants.SERVER_PORT_ENV;
import static com.treep.util.constants.ConfigConstants.SERVER_THREAD_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;


class GatewayConfigReaderTest {

    private static final String EXPECTED_PORT = "5050";

    private static final String EXPECTED_CONFIG_FILE_NAME = "test/location/file.yml";

    private static final String EXPECTED_THREAD_COUNT = "100";

    @Test
    void shouldCorrectlySetDefaultsOnNoEnv() {
        GatewayConfig gatewayConfig = GatewayConfigReader.readEnv();

        assertEquals(DEFAULT_SERVER_PORT, gatewayConfig.getServerPort());
        assertEquals(DEFAULT_GATEWAY_CONFIG_LOCATION, gatewayConfig.getConfigLocation());
    }

    @Test
    @SetEnvironmentVariable(key = SERVER_PORT_ENV, value = EXPECTED_PORT)
    void shouldCorrectlyReadPortFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        assertEquals(EXPECTED_PORT, actualConfig.getServerPort());
    }

    @Test
    @SetEnvironmentVariable(key = GATEWAY_CONFIG_LOCATION_ENV, value = EXPECTED_CONFIG_FILE_NAME)
    void shouldCorrectlyReadFileLocationFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        assertEquals(EXPECTED_CONFIG_FILE_NAME, actualConfig.getConfigLocation());
    }

    @Test
    @SetEnvironmentVariable(key = SERVER_THREAD_COUNT, value = EXPECTED_THREAD_COUNT)
    void shouldCorrectlyReadThreadCountFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        assertEquals(EXPECTED_THREAD_COUNT, actualConfig.getServerThreadCount());
    }

    @Test
    @SetEnvironmentVariable.SetEnvironmentVariables({
            @SetEnvironmentVariable(key = GATEWAY_CONFIG_LOCATION_ENV, value = EXPECTED_CONFIG_FILE_NAME),
            @SetEnvironmentVariable(key = SERVER_PORT_ENV, value = EXPECTED_PORT),
            @SetEnvironmentVariable(key = SERVER_THREAD_COUNT, value = EXPECTED_THREAD_COUNT)
    })
    void shouldCorrectlyReadAllConfigurationsFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        assertEquals(EXPECTED_CONFIG_FILE_NAME, actualConfig.getConfigLocation());
        assertEquals(EXPECTED_PORT, actualConfig.getServerPort());
        assertEquals(EXPECTED_THREAD_COUNT, actualConfig.getServerThreadCount());
    }
}
