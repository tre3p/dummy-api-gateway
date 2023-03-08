package integration;

import com.treep.Main;
import com.treep.exception.ConfigurationReadingException;
import com.treep.exception.RoutesValidationException;
import com.treep.util.constants.ConfigConstants;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import util.Utils;

import java.io.IOException;

import static util.TestConstants.TEST_GATEWAY_CONFIG_PATH;

@SetEnvironmentVariable.SetEnvironmentVariables({
        @SetEnvironmentVariable(key = ConfigConstants.GATEWAY_CONFIG_LOCATION_ENV, value = TEST_GATEWAY_CONFIG_PATH)
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class WiremockAbstractTest {

    private static Thread serverThread;

    @BeforeAll
    void beforeAll() {
        serverThread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Main.main(new String[]{});
            }
        });

        serverThread.start();
    }

    @AfterAll
    void afterAll() {
        serverThread.interrupt();
    }
}
