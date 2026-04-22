package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public final class SphericalVariation implements Variation {
    @Override
    public String name() {
        return "spherical";
    }

    @Override
    public Point2D apply(Point2D point, Map<String, Double> params) {
        double r2 = point.x() * point.x() + point.y() * point.y();
        if (r2 < 1e-12) {
            return point;
        }
        return new Point2D(point.x() / r2, point.y() / r2);
    }
}
