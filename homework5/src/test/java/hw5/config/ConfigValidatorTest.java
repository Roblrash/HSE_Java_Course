package hw5.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hw5.model.AffineTransform;
import hw5.model.FunctionSpec;
import hw5.model.Size;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConfigValidatorTest {
    @Test
    void shouldValidateCorrectConfig() {
        GenerationConfig config = new GenerationConfig(
            new Size(400, 300),
            1000,
            "a.png",
            2,
            1L,
            List.of(new FunctionSpec("linear", 1.0, Map.of())),
            List.of(new AffineTransform(1, 0, 0, 0, 1, 0)),
            1,
            20,
            2.2
        );

        ConfigValidator.validateOrThrow(config);
    }

    @Test
    void shouldThrowOnInvalidConfig() {
        GenerationConfig config = new GenerationConfig(
            new Size(0, 300),
            0,
            "",
            0,
            1L,
            List.of(),
            List.of(),
            0,
            -1,
            0.0
        );

        assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateOrThrow(config));
    }

    @Test
    void shouldApplyOverrides() {
        GenerationConfig config = GenerationConfig.defaults().withOverrides(
            new PartialConfig(
                new Size(100, 200),
                999,
                "b.png",
                3,
                10L,
                null,
                null,
                4,
                10,
                1.8
            )
        );

        assertEquals(100, config.size().width());
        assertEquals(200, config.size().height());
        assertEquals(999, config.iterationCount());
        assertEquals("b.png", config.outputPath());
        assertEquals(3, config.threads());
        assertEquals(10L, config.seed());
        assertEquals(4, config.symmetryLevel());
        assertEquals(10, config.warmupIterations());
        assertEquals(1.8, config.gamma());
    }
}
