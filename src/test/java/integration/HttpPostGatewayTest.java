package integration;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.treep.util.constants.HttpConstants;
import org.junit.jupiter.api.Test;
import util.TestConstants;
import util.Utils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static util.TestConstants.TEST_JSON_STUB;
import static util.TestConstants.TEST_SOURCE_URL;
import static util.TestConstants.TEST_TARGET_ENDPOINT;

@WireMockTest(httpPort = 5050)
public class HttpPostGatewayTest extends WiremockAbstractTest {

    @Test
    void shouldCorrectlyPassJsonToMockServer() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(TestConstants.TEST_SOURCE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(TestConstants.TEST_JSON_STUB))
                .header(HttpConstants.CONTENT_TYPE, "application/json")
                .build();

        Utils.executeRequest(req);

        verify(
                postRequestedFor(urlEqualTo(TEST_TARGET_ENDPOINT))
                        .withHeader(HttpConstants.CONTENT_TYPE, equalTo("application/json"))
                        .withRequestBody(equalToJson(TEST_JSON_STUB))
        );
    }

    @Test
    void shouldCorrectlyPassXWwwFormUrlEncodedToMockServer() throws IOException, URISyntaxException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                        .uri(new URI(TEST_SOURCE_URL))
                        .POST(HttpRequest.BodyPublishers.ofString("foo=bar&bar=baz"))
                        .header(HttpConstants.CONTENT_TYPE, "application/x-www-form-urlencoded")
                        .build();

        Utils.executeRequest(req);

        verify(
                postRequestedFor(urlEqualTo(TEST_TARGET_ENDPOINT))
                        .withRequestBody(equalTo("foo=bar&bar=baz"))
                        .withHeader("Content-Type", equalToIgnoreCase("application/x-www-form-urlencoded"))
        );
    }
}
