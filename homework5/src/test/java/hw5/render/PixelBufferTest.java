package hw5.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hw5.model.RgbColor;
import org.junit.jupiter.api.Test;

class PixelBufferTest {
    @Test
    void shouldMergeBuffers() {
        PixelBuffer a = new PixelBuffer(2, 2);
        PixelBuffer b = new PixelBuffer(2, 2);

        a.add(0, 0, new RgbColor(1, 0, 0));
        b.add(0, 0, new RgbColor(0, 1, 0));

        a.merge(b);

        assertEquals(1.0, a.r()[0]);
        assertEquals(1.0, a.g()[0]);
        assertEquals(0.0, a.b()[0]);
        assertEquals(2, a.hits()[0]);
    }
}
