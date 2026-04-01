package spacex.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import spacex.model.Launch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonBuilderTest {
    private final JsonBuilder jsonBuilder = new JsonBuilder();
    private final spacex.json.JsonParser launchParser = new spacex.json.JsonParser();

    @Test
    void toJsonSerializesSingleLaunch() throws IOException {
        Launch launch = launchParser.parseLaunch(readResource("launch_single.json"));

        String json = jsonBuilder.toJson(launch);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        assertEquals("FalconSat", root.get("name").getAsString());
        assertEquals(1, root.get("flight_number").getAsInt());
    }

    @Test
    void toJsonSerializesLaunchList() throws IOException {
        List<Launch> launches = launchParser.parseLaunches(readResource("launches_array.json"));

        String json = jsonBuilder.toJson(launches);
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();

        assertEquals(2, array.size());
    }

    @Test
    void toJsonKeepsNullFields() throws IOException {
        Launch launch = launchParser.parseLaunch(readResource("launch_with_nulls.json"));

        String json = jsonBuilder.toJson(launch);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        assertTrue(root.has("details"));
        assertTrue(root.get("details").isJsonNull());
    }

    @Test
    void buildDateRangeQueryBuildsExpectedBody() {
        String json = jsonBuilder.buildDateRangeQuery("2020-01-01", "2020-12-31");
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        JsonObject query = root.getAsJsonObject("query");
        JsonObject dateUtc = query.getAsJsonObject("date_utc");

        assertEquals("2020-01-01T00:00:00.000Z", dateUtc.get("$gte").getAsString());
        assertEquals("2020-12-31T23:59:59.999Z", dateUtc.get("$lte").getAsString());
    }

    @Test
    void buildSuccessQueryBuildsExpectedBody() {
        String json = jsonBuilder.buildSuccessQuery(true);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        assertTrue(root.getAsJsonObject("query").get("success").getAsBoolean());
        assertEquals("asc", root.getAsJsonObject("options").getAsJsonObject("sort").get("flight_number").getAsString());
    }

    private String readResource(String name) throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(name)) {
            assertNotNull(stream, "Resource not found: " + name);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
