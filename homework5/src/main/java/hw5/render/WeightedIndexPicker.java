package hw5.render;

import java.util.Arrays;

public final class WeightedIndexPicker {
    private final double[] cumulative;

    public WeightedIndexPicker(double[] weights) {
        this.cumulative = new double[weights.length];
        double sum = 0.0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            cumulative[i] = sum;
        }
        if (sum <= 0.0 || !Double.isFinite(sum)) {
            throw new IllegalArgumentException("Invalid weights sum: " + sum);
        }
        for (int i = 0; i < cumulative.length; i++) {
            cumulative[i] /= sum;
        }
        cumulative[cumulative.length - 1] = 1.0;
    }

    public int pick(double random01) {
        int idx = Arrays.binarySearch(cumulative, random01);
        if (idx >= 0) {
            return idx;
        }
        int insertion = -idx - 1;
        return Math.min(insertion, cumulative.length - 1);
    }
}
