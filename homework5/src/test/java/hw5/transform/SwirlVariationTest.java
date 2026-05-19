package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import hw5.model.Point2D;
import org.junit.jupiter.api.Test;

class SwirlVariationTest {
    private static final double EPS = 1e-9;

    @Test
    void shouldApplySwirlFormula() {
        var v = new SwirlVariation();
        Point2D p = new Point2D(0.2, -0.3);
        double r2 = p.x() * p.x() + p.y() * p.y();
        Point2D out = v.apply(p, Map.of());

        double expectedX = p.x() * Math.sin(r2) - p.y() * Math.cos(r2);
        double expectedY = p.x() * Math.cos(r2) + p.y() * Math.sin(r2);

        assertEquals(expectedX, out.x(), EPS);
        assertEquals(expectedY, out.y(), EPS);
    }
}
