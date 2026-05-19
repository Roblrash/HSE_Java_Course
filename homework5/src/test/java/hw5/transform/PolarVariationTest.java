package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import hw5.model.Point2D;
import org.junit.jupiter.api.Test;

class PolarVariationTest {
    private static final double EPS = 1e-9;

    @Test
    void shouldApplyPolarFormula() {
        var v = new PolarVariation();
        Point2D p = new Point2D(1.0, 1.0);
        Point2D out = v.apply(p, Map.of());

        assertEquals(Math.atan2(1.0, 1.0) / Math.PI, out.x(), EPS);
        assertEquals(Math.hypot(1.0, 1.0) - 1.0, out.y(), EPS);
    }
}
