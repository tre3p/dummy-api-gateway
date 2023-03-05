package unit.config;

import com.treep.config.GatewayConfigReader;
import com.treep.config.model.GatewayConfig;
import com.treep.util.constants.ConfigConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

public class GatewayConfigReaderTest {

    private static final String EXPECTED_PORT = "5050";

    private static final String EXPECTED_CONFIG_FILE_NAME = "test/location/file.yml";

    @Test
    void shouldCorrectlySetDefaultsOnNoEnv() {
        GatewayConfig gatewayConfig = GatewayConfigReader.readEnv();

        Assertions.assertEquals(ConfigConstants.DEFAULT_SERVER_PORT, gatewayConfig.getServerPort());
        Assertions.assertEquals(ConfigConstants.DEFAULT_GATEWAY_CONFIG_LOCATION, gatewayConfig.getConfigLocation());
    }

    @Test
    @SetEnvironmentVariable(key = ConfigConstants.SERVER_PORT_ENV, value = EXPECTED_PORT)
    void shouldCorrectlyReadPortFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        Assertions.assertEquals(Integer.parseInt(EXPECTED_PORT), actualConfig.getServerPort());
    }

    @Test
    @SetEnvironmentVariable(key = ConfigConstants.GATEWAY_CONFIG_LOCATION_ENV, value = EXPECTED_CONFIG_FILE_NAME)
    void shouldCorrectlyReadFileLocationFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        Assertions.assertEquals(EXPECTED_CONFIG_FILE_NAME, actualConfig.getConfigLocation());
    }

    @Test
    @SetEnvironmentVariable.SetEnvironmentVariables({
            @SetEnvironmentVariable(key = ConfigConstants.GATEWAY_CONFIG_LOCATION_ENV, value = EXPECTED_CONFIG_FILE_NAME),
            @SetEnvironmentVariable(key =  ConfigConstants.SERVER_PORT_ENV, value = EXPECTED_PORT)
    })
    void shouldCorrectlyReadServerPortAndFileNameFromEnv() {
        GatewayConfig actualConfig = GatewayConfigReader.readEnv();

        Assertions.assertEquals(EXPECTED_CONFIG_FILE_NAME, actualConfig.getConfigLocation());
        Assertions.assertEquals(Integer.parseInt(EXPECTED_PORT), actualConfig.getServerPort());
    }
}
