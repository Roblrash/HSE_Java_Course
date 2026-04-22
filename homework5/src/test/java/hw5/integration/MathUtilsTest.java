package hw5.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hw5.util.MathUtils;
import org.junit.jupiter.api.Test;

class MathUtilsTest {
    @Test
    void shouldClampValues() {
        assertEquals(0.0, MathUtils.clamp(-1.0, 0.0, 1.0));
        assertEquals(0.5, MathUtils.clamp(0.5, 0.0, 1.0));
        assertEquals(1.0, MathUtils.clamp(2.0, 0.0, 1.0));
    }
}
