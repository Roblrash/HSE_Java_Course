package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public final class LinearVariation implements Variation {
    @Override
    public String name() {
        return "linear";
    }

    @Override
    public Point2D apply(Point2D point, Map<String, Double> params) {
        return point;
    }
}
