package util;

import com.treep.Main;
import com.treep.exception.ConfigurationReadingException;
import com.treep.exception.RoutesValidationException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Utility class for storing constants or common methods
 */
public class Utils {

    public static final String FIXTURES_PATH = "src/test/resources/fixtures/";

    public static final String TEST_GATEWAY_CONFIG_PATH = "src/test/resources/gateway-config-test.yml";

    private static final HttpClient httpClient = HttpClient.newBuilder().build();

    public static HttpResponse<byte[]> executeRequest(HttpRequest req) throws IOException, InterruptedException {
        return httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
    }

    public static void startServer() throws ConfigurationReadingException, RoutesValidationException, IOException {
        Main.main(new String[]{});
    }
}
