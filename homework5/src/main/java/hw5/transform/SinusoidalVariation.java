package hw5.transform;

import java.util.Map;
import hw5.model.Point2D;

public final class SinusoidalVariation implements Variation {
    @Override
    public String name() {
        return "sinusoidal";
    }

    @Override
    public Point2D apply(Point2D point, Map<String, Double> params) {
        return new Point2D(Math.sin(point.x()), Math.sin(point.y()));
    }
}
