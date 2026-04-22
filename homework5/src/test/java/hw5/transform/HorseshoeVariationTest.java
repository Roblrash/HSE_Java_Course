package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import hw5.model.Point2D;
import org.junit.jupiter.api.Test;

class HorseshoeVariationTest {
    private static final double EPS = 1e-9;

    @Test
    void shouldApplyHorseshoeFormula() {
        var v = new HorseshoeVariation();
        Point2D p = new Point2D(0.4, 0.3);
        double r = Math.hypot(p.x(), p.y());
        Point2D out = v.apply(p, Map.of());

        double expectedX = ((p.x() - p.y()) * (p.x() + p.y())) / r;
        double expectedY = (2.0 * p.x() * p.y()) / r;

        assertEquals(expectedX, out.x(), EPS);
        assertEquals(expectedY, out.y(), EPS);
    }
}
