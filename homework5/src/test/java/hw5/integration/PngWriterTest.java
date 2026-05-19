package hw5.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hw5.io.PngWriter;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PngWriterTest {
    @Test
    void shouldWritePng() throws Exception {
        Path file = Files.createTempFile("png-writer", ".png");
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        new PngWriter().write(image, file.toString());

        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 0);
    }
}
