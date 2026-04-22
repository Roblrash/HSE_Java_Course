package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public interface Variation {
    String name();

    Point2D apply(Point2D point, Map<String, Double> params);
}
