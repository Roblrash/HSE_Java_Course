package hw5.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class JsonConfigLoaderTest {
    @Test
    void shouldParseJsonConfig() throws Exception {
        Path json = Files.createTempFile("hw5-json", ".json");
        Files.writeString(json, """
            {
              "size": {"width": 1920, "height": 1080},
              "iteration_count": 2500,
              "output_path": "result.png",
              "threads": 4,
              "seed": 2.1324512,
              "symmetry_level": 5,
              "functions": [
                {"name": "swirl", "weight": 1.0, "params": {"k": 0.5}},
                {"name": "horseshoe", "weight": 0.7}
              ],
              "affine_params": [
                {"a": 1.0, "b": 1.0, "c": 1.0, "d": 1.0, "e": 1.0, "f": 1.0}
              ]
            }
            """);

        JsonConfigLoader loader = new JsonConfigLoader();
        PartialConfig config = loader.load(json);

        assertEquals(1920, config.size().width());
        assertEquals(1080, config.size().height());
        assertEquals(2500, config.iterationCount());
        assertEquals("result.png", config.outputPath());
        assertEquals(4, config.threads());
        assertEquals(2L, config.seed());
        assertEquals(5, config.symmetryLevel());
        assertNotNull(config.functions());
        assertEquals(2, config.functions().size());
        assertEquals(1, config.affineParams().size());
    }
}
