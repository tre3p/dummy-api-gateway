package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.treep.config.RoutesConfigReader;
import com.treep.config.model.RouteDefinition;
import com.treep.exception.ConfigurationReadingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static util.Utils.FIXTURES_PATH;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoutesConfigReaderTest {

    private RoutesConfigReader routesConfigReader;

    private final String correctConfigPath = FIXTURES_PATH + "gateway-config-test-correct.yml";

    private final String invalidConfigPath = FIXTURES_PATH + "gateway-config-test-invalid.yml";

    private final String notExistsConfigPath = FIXTURES_PATH + "gateway-config-test-not-exists.yml";

    private final Map<String, RouteDefinition> expectedMap = Map.of(
            "/sample-endpoint", new RouteDefinition(
                    "http://localhost:8080",
                    "/sample-endpoint",
                    10,
                    400
            ),
            "/sample-url", new RouteDefinition(
                    "http://localhost:9090",
                    "/sample-url",
                    20,
                    500
            )
    );

    @BeforeAll
    void beforeAll() {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        routesConfigReader = new RoutesConfigReader(om);
    }

    @Test
    void shouldCorrectlyReadMultipleConfigurationsFromYAML() {
        Map<String, RouteDefinition> result = routesConfigReader.readRouteProperties(correctConfigPath);

        Assertions.assertEquals(expectedMap, result);
    }

    @Test
    void shouldThrowExceptionOnNonUniqueSourceEndpoints() {
        Assertions.assertThrows(
                ConfigurationReadingException.class,
                () -> routesConfigReader.readRouteProperties(invalidConfigPath)
                );
    }

    @Test
    void shouldThrowExceptionOnNotExistsConfigFilePath() {
        Assertions.assertThrows(
                ConfigurationReadingException.class,
                () -> routesConfigReader.readRouteProperties(notExistsConfigPath)
        );
    }


}
