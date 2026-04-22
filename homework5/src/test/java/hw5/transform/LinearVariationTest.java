package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import hw5.model.Point2D;
import org.junit.jupiter.api.Test;

class LinearVariationTest {
    private static final double EPS = 1e-9;

    @Test
    void shouldReturnSamePoint() {
        var v = new LinearVariation();
        Point2D p = new Point2D(0.7, -0.2);
        Point2D out = v.apply(p, Map.of());

        assertEquals(0.7, out.x(), EPS);
        assertEquals(-0.2, out.y(), EPS);
    }
}
