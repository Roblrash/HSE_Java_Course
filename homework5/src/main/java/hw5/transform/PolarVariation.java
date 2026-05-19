package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public final class PolarVariation implements Variation {
    @Override
    public String name() {
        return "polar";
    }

    @Override
    public Point2D apply(Point2D point, Map<String, Double> params) {
        double theta = Math.atan2(point.y(), point.x());
        double r = Math.hypot(point.x(), point.y());
        return new Point2D(theta / Math.PI, r - 1.0);
    }
}
