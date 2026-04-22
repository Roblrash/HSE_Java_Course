package hw5.model;

import hw5.util.MathUtils;

public record RgbColor(double r, double g, double b) {
    public static RgbColor fromIndex(double idx) {
        // HSV-like rainbow map; idx in [0,1].
        double h = idx - Math.floor(idx);
        double six = h * 6.0;
        int i = (int) Math.floor(six);
        double f = six - i;
        double q = 1.0 - f;
        return switch (i % 6) {
            case 0 -> new RgbColor(1.0, f, 0.0);
            case 1 -> new RgbColor(q, 1.0, 0.0);
            case 2 -> new RgbColor(0.0, 1.0, f);
            case 3 -> new RgbColor(0.0, q, 1.0);
            case 4 -> new RgbColor(f, 0.0, 1.0);
            default -> new RgbColor(1.0, 0.0, q);
        };
    }

    public RgbColor scale(double k) {
        return new RgbColor(r * k, g * k, b * k);
    }

    public int toRgbInt() {
        int rr = (int) Math.round(MathUtils.clamp(r, 0.0, 1.0) * 255.0);
        int gg = (int) Math.round(MathUtils.clamp(g, 0.0, 1.0) * 255.0);
        int bb = (int) Math.round(MathUtils.clamp(b, 0.0, 1.0) * 255.0);
        return (rr << 16) | (gg << 8) | bb;
    }
}
