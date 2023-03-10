package integration;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import util.Utils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.TestConstants.TEST_JSON_STUB;
import static util.TestConstants.TEST_SOURCE_URL;
import static util.TestConstants.TEST_TARGET_ENDPOINT;
import static util.Utils.executeRequest;

@WireMockTest(httpPort = 5050)
class HttpGetGatewayTest extends WiremockAbstractTest {

    private static final String TEST_QUERY_PARAM = "?foo=bar&bar=baz";

    private static final String TEST_HEADER_KEY = "X-Custom-Header";

    private static final String TEST_HEADER_VALUE = "FooBarBaz";

    @Test
    void shouldCorrectlyProxyDefaultGetRequestToTargetUrl()
            throws URISyntaxException, IOException, InterruptedException
    {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(TEST_SOURCE_URL))
                .GET()
                .build();

        Utils.executeRequest(req);
        verify(getRequestedFor(urlEqualTo(TEST_TARGET_ENDPOINT)));
    }

    @Test
    void shouldCorrectlyProxyQueryParamGetRequestToTargetUrl()
            throws URISyntaxException, IOException, InterruptedException
    {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(TEST_SOURCE_URL + TEST_QUERY_PARAM))
                .GET()
                .build();

        Utils.executeRequest(req);

        verify(
                getRequestedFor(urlEqualTo(TEST_TARGET_ENDPOINT + TEST_QUERY_PARAM))
                        .withQueryParam("foo", equalTo("bar"))
                        .withQueryParam("bar", equalTo("baz"))
        );
    }

    @Test
    void shouldCorrectlyProxyGetRequestAndRetrieveResponseBody() throws URISyntaxException, IOException, InterruptedException {
        configureStubForJsonResponse();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(TEST_SOURCE_URL))
                .GET()
                .build();

        HttpResponse<byte[]> resp = executeRequest(req);
        String responseBody = new String(resp.body(), StandardCharsets.UTF_8);
        int statusCode = resp.statusCode();

        verify(
                getRequestedFor(urlEqualTo(TEST_TARGET_ENDPOINT))
        );
        assertEquals(TEST_JSON_STUB, responseBody);
        assertEquals(200, statusCode);
    }

    @Test
    void shouldCorrectlyProxyGetRequestAndRetrieveCustomHeader() throws URISyntaxException, IOException, InterruptedException {
        configureStubForResponseContainsCustomHeaders();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(TEST_SOURCE_URL))
                .GET()
                .build();

        HttpResponse<byte[]> resp = executeRequest(req);

        String actualHeaderValue = resp.headers().map().get(TEST_HEADER_KEY).get(0);

        assertEquals(TEST_HEADER_VALUE, actualHeaderValue);
    }

    @Test
    void shouldReturnBadRequestOnRequestTimeoutExceeded() throws URISyntaxException, IOException, InterruptedException {
        configureStubForResponseAfter10SecondsTimeout();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(TEST_SOURCE_URL))
                .GET()
                .build();

        HttpResponse<byte[]> resp = Utils.executeRequest(req);

        assertEquals(400, resp.statusCode());
    }

    private void configureStubForResponseAfter10SecondsTimeout() {
        stubFor(
                get(urlEqualTo(TEST_TARGET_ENDPOINT))
                        .willReturn(aResponse().withStatus(200).withFixedDelay(10000))
        );
    }

    private void configureStubForResponseContainsCustomHeaders() {
        stubFor(
                get(urlEqualTo(TEST_TARGET_ENDPOINT))
                        .willReturn(aResponse().withHeader(TEST_HEADER_KEY, TEST_HEADER_VALUE))
        );
    }

    private void configureStubForJsonResponse() {
        stubFor(
                get(urlEqualTo(TEST_TARGET_ENDPOINT))
                        .willReturn(aResponse().withBody(TEST_JSON_STUB).withStatus(200))
        );
    }
}
