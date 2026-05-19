package hw5.model;

public record AffineTransform(double a, double b, double c, double d, double e, double f) {
    public Point2D apply(Point2D point) {
        double x = a * point.x() + b * point.y() + c;
        double y = d * point.x() + e * point.y() + f;
        return new Point2D(x, y);
    }
}
