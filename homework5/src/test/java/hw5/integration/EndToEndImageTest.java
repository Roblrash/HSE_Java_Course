package hw5.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hw5.config.GenerationConfig;
import hw5.config.PartialConfig;
import hw5.io.PngWriter;
import hw5.model.Size;
import hw5.render.FlameRenderer;
import hw5.transform.VariationRegistry;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class EndToEndImageTest {
    @Test
    void shouldSavePngWithExpectedDimensions() throws Exception {
        Path file = Files.createTempFile("flame-result", ".png");

        GenerationConfig config = GenerationConfig.defaults().withOverrides(
            new PartialConfig(
                new Size(240, 160),
                50_000,
                file.toString(),
                2,
                42L,
                null,
                null,
                2,
                20,
                2.2
            )
        );

        FlameRenderer renderer = new FlameRenderer(new VariationRegistry());
        var result = renderer.render(config);
        new PngWriter().write(result.image(), config.outputPath());

        assertTrue(Files.exists(file));
        BufferedImage img = ImageIO.read(file.toFile());
        assertEquals(240, img.getWidth());
        assertEquals(160, img.getHeight());
    }
}
