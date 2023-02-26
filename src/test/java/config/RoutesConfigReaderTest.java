package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.treep.config.RoutesConfigReader;
import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.ConfigurationReadingException;
import com.treep.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static util.Utils.FIXTURES_PATH;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoutesConfigReaderTest {

    private RoutesConfigReader routesConfigReader;

    private static final String correctConfigPath = FIXTURES_PATH + "gateway-config-test-correct.yml";

    private static final String notExistsConfigPath = FIXTURES_PATH + "gateway-config-test-not-exists.yml";

    private final Routes expectedRoutes = new Routes(
            List.of(
                    new RouteDefinition(
                            "http://localhost:8080",
                            "/sample-endpoint",
                            10,
                            400
                    ),
                    new RouteDefinition(
                            "http://localhost:9090",
                            "/sample-url",
                            20,
                            500
                    )
            ));

    @BeforeAll
    void beforeAll() {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        routesConfigReader = new RoutesConfigReader(om);
    }

    @Test
    void shouldCorrectlyReadMultipleConfigurationsFromYAML() throws ConfigurationReadingException {
        Routes actual = routesConfigReader.readRouteProperties(correctConfigPath);

        Assertions.assertEquals(expectedRoutes, actual);
    }

    @Test
    void shouldThrowExceptionOnNotExistsConfigFilePath() {
        Assertions.assertThrows(
                ConfigurationReadingException.class,
                () -> routesConfigReader.readRouteProperties(notExistsConfigPath),
                Constants.ERROR_CONFIG_READING
        );
    }

    @Test
    void shouldThrowExceptionOnNullPassedInArgs() {
        Assertions.assertThrows(
                ConfigurationReadingException.class,
                () -> routesConfigReader.readRouteProperties(null)
        );
    }
}
