package spacex.json;

import org.junit.jupiter.api.Test;
import spacex.model.Launch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {
    private final JsonParser parser = new JsonParser();

    @Test
    void parseLaunchDeserializesSingleObject() throws IOException {
        String json = readResource("launch_single.json");

        Launch launch = parser.parseLaunch(json);

        assertEquals("5eb87cd9ffd86e000604b32a", launch.getId());
        assertEquals("FalconSat", launch.getName());
        assertEquals(1, launch.getFlightNumber());
        assertEquals("2006-03-24T22:30:00.000Z", launch.getDateUtc());
        assertFalse(Boolean.TRUE.equals(launch.getSuccess()));
    }

    @Test
    void parseLaunchesDeserializesArray() throws IOException {
        String json = readResource("launches_array.json");

        List<Launch> launches = parser.parseLaunches(json);

        assertEquals(2, launches.size());
        assertEquals(2, launches.get(1).getFlightNumber());
    }

    @Test
    void parseLaunchHandlesNullValues() throws IOException {
        String json = readResource("launch_with_nulls.json");

        Launch launch = parser.parseLaunch(json);

        assertNull(launch.getSuccess());
        assertNull(launch.getDetails());
        assertNull(launch.getFailures().get(0).getAltitude());
    }

    @Test
    void parseLaunchDeserializesNestedObjects() throws IOException {
        String json = readResource("launch_single.json");

        Launch launch = parser.parseLaunch(json);

        assertEquals(1, launch.getFailures().size());
        assertEquals("merlin engine failure", launch.getFailures().get(0).getReason());
        assertEquals(1, launch.getCores().size());
        assertEquals("5e9e289df35918033d3b2623", launch.getCores().get(0).getCore());
    }

    @Test
    void parseLaunchMapsSerializedNameFields() throws IOException {
        String json = readResource("launch_single.json");

        Launch launch = parser.parseLaunch(json);

        assertEquals(1, launch.getFlightNumber());
        assertEquals("2006-03-24T22:30:00.000Z", launch.getDateUtc());
    }

    @Test
    void parseLaunchThrowsForInvalidJson() throws IOException {
        String json = readResource("invalid_launch.json");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> parser.parseLaunch(json));

        assertTrue(ex.getMessage().contains("Некорректный JSON"));
    }

    @Test
    void parseQueryLaunchesReadsDocsArray() throws IOException {
        String json = readResource("query_response.json");

        List<Launch> launches = parser.parseQueryLaunches(json);

        assertEquals(1, launches.size());
        assertEquals("CRS-20", launches.get(0).getName());
    }

    private String readResource(String name) throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(name)) {
            assertNotNull(stream, "Resource not found: " + name);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
