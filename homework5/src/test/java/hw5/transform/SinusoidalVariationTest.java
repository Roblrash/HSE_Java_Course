package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import hw5.model.Point2D;
import org.junit.jupiter.api.Test;

class SinusoidalVariationTest {
    private static final double EPS = 1e-9;

    @Test
    void shouldApplySinToEachCoordinate() {
        var v = new SinusoidalVariation();
        Point2D out = v.apply(new Point2D(Math.PI / 2.0, 0.0), Map.of());

        assertEquals(1.0, out.x(), EPS);
        assertEquals(0.0, out.y(), EPS);
    }
}
