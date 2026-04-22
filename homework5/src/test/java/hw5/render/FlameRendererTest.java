package hw5.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hw5.config.GenerationConfig;
import hw5.config.PartialConfig;
import hw5.model.Size;
import hw5.transform.VariationRegistry;
import org.junit.jupiter.api.Test;

class FlameRendererTest {
    @Test
    void shouldRenderImage() {
        FlameRenderer renderer = new FlameRenderer(new VariationRegistry());
        GenerationConfig config = GenerationConfig.defaults().withOverrides(
            new PartialConfig(
                new Size(300, 200),
                30_000,
                "test.png",
                1,
                3L,
                null,
                null,
                1,
                20,
                2.2
            )
        );

        RenderedImage result = renderer.render(config);

        assertEquals(300, result.image().getWidth());
        assertEquals(200, result.image().getHeight());
        assertTrue(result.elapsedMillis() >= 0);
    }
}
