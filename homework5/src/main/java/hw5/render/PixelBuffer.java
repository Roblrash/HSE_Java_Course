package hw5.render;

import hw5.model.RgbColor;

public final class PixelBuffer {
    private final int width;
    private final int height;
    private final double[] r;
    private final double[] g;
    private final double[] b;
    private final int[] hits;

    public PixelBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        this.r = new double[size];
        this.g = new double[size];
        this.b = new double[size];
        this.hits = new int[size];
    }

    public void add(int x, int y, RgbColor color) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        int idx = y * width + x;
        r[idx] += color.r();
        g[idx] += color.g();
        b[idx] += color.b();
        hits[idx] += 1;
    }

    public void merge(PixelBuffer other) {
        if (other.width != width || other.height != height) {
            throw new IllegalArgumentException("Buffers have different dimensions");
        }
        for (int i = 0; i < hits.length; i++) {
            this.r[i] += other.r[i];
            this.g[i] += other.g[i];
            this.b[i] += other.b[i];
            this.hits[i] += other.hits[i];
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double[] r() {
        return r;
    }

    public double[] g() {
        return g;
    }

    public double[] b() {
        return b;
    }

    public int[] hits() {
        return hits;
    }
}
