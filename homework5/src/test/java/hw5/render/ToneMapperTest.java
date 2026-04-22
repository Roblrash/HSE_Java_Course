package hw5.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import hw5.model.RgbColor;
import org.junit.jupiter.api.Test;

class ToneMapperTest {
    @Test
    void shouldCreateImageWithExpectedSize() {
        PixelBuffer buffer = new PixelBuffer(3, 2);
        buffer.add(1, 1, new RgbColor(1, 0.5, 0.25));

        BufferedImage image = ToneMapper.toImage(buffer, 2.2);

        assertEquals(3, image.getWidth());
        assertEquals(2, image.getHeight());
    }
}
