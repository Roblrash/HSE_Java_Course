package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import hw5.model.Point2D;
import org.junit.jupiter.api.Test;

class SphericalVariationTest {
    private static final double EPS = 1e-9;

    @Test
    void shouldDivideByRadiusSquare() {
        var v = new SphericalVariation();
        Point2D out = v.apply(new Point2D(1.0, 2.0), Map.of());

        assertEquals(0.2, out.x(), EPS);
        assertEquals(0.4, out.y(), EPS);
    }
}
