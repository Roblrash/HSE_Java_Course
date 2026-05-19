package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public final class HorseshoeVariation implements Variation {
    @Override
    public String name() {
        return "horseshoe";
    }

    @Override
    public Point2D apply(Point2D point, Map<String, Double> params) {
        double r = Math.hypot(point.x(), point.y());
        if (r < 1e-12) {
            return point;
        }
        double x = ((point.x() - point.y()) * (point.x() + point.y())) / r;
        double y = (2.0 * point.x() * point.y()) / r;
        return new Point2D(x, y);
    }
}
