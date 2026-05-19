package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public final class SwirlVariation implements Variation {
    @Override
    public String name() {
        return "swirl";
    }

    @Override
    public Point2D apply(Point2D point, Map<String, Double> params) {
        double r2 = point.x() * point.x() + point.y() * point.y();
        double sin = Math.sin(r2);
        double cos = Math.cos(r2);
        double x = point.x() * sin - point.y() * cos;
        double y = point.x() * cos + point.y() * sin;
        return new Point2D(x, y);
    }
}
