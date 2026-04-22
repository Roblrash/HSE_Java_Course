package hw5.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hw5.cli.CliParseResult;
import hw5.model.Size;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ConfigResolverTest {
    @Test
    void shouldUsePriorityCliOverJsonOverDefaults() throws Exception {
        Path json = Files.createTempFile("hw5-config", ".json");
        Files.writeString(json, """
            {
              "size": {"width": 111, "height": 222},
              "iteration_count": 500,
              "threads": 2,
              "seed": 77,
              "output_path": "json.png",
              "functions": [{"name": "swirl", "weight": 1.0}],
              "affine_params": [{"a": 0.5, "b":0, "c":0, "d":0, "e":0.5, "f":0}]
            }
            """);

        PartialConfig cliPartial = new PartialConfig(
            new Size(333, 444),
            null,
            "cli.png",
            4,
            null,
            null,
            null,
            null,
            null,
            null
        );

        ConfigResolver resolver = new ConfigResolver(new JsonConfigLoader());
        GenerationConfig cfg = resolver.resolve(new CliParseResult(cliPartial, json.toString()));

        assertEquals(333, cfg.size().width());
        assertEquals(444, cfg.size().height());
        assertEquals(500, cfg.iterationCount());
        assertEquals("cli.png", cfg.outputPath());
        assertEquals(4, cfg.threads());
        assertEquals(77L, cfg.seed());
    }
}
