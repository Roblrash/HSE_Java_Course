package spacex.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpaceXHttpClientTest {
    private final SpaceXHttpClient client = new SpaceXHttpClient();

    @Test
    void buildUrlBuildsFromParts() {
        String url = SpaceXHttpClient.buildUrl("https://api.spacexdata.com/", "v5/launches");
        assertEquals("https://api.spacexdata.com/v5/launches", url);
    }

    @Test
    void handleResponseReturnsBodyFor200() throws Exception {
        String result = client.handleResponse(200, "{\"ok\":true}");
        assertEquals("{\"ok\":true}", result);
    }

    @Test
    void handleResponseThrowsForHttpError() {
        HttpRequestException ex = assertThrows(HttpRequestException.class,
                () -> client.handleResponse(404, "{\"error\":\"not found\"}"));

        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getResponseBody().contains("not found"));
    }
}
