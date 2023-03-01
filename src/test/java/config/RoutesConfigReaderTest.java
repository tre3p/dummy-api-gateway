package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.treep.config.RoutesConfigReader;
import com.treep.config.model.RouteDefinition;
import com.treep.config.model.Routes;
import com.treep.exception.ConfigurationReadingException;
import com.treep.util.ErrorConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                            10
                    ),
                    new RouteDefinition(
                            "http://localhost:9090",
                            "/sample-url",
                            20
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

        assertEquals(expectedRoutes, actual);
    }

    @Test
    void shouldThrowExceptionOnNotExistsConfigFilePath() {
        assertThrows(
                ConfigurationReadingException.class,
                () -> routesConfigReader.readRouteProperties(notExistsConfigPath),
                ErrorConstants.CONFIG_READING_ERROR
        );
    }

    @Test
    void shouldThrowExceptionOnNullPassedInArgs() {
        assertThrows(
                ConfigurationReadingException.class,
                () -> routesConfigReader.readRouteProperties(null)
        );
    }
}
