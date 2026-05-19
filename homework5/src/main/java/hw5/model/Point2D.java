package hw5.model;

public record Point2D(double x, double y) {
    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y);
    }
}
