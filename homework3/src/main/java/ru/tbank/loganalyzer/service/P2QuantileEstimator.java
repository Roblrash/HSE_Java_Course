package ru.tbank.loganalyzer.service;

import java.util.Arrays;

public class P2QuantileEstimator {
    private final double quantile;

    private final double[] markerHeights = new double[5];
    private final int[] markerPositions = new int[5];
    private final double[] desiredPositions = new double[5];
    private final double[] positionIncrements = new double[5];

    private final double[] initial = new double[5];
    private int count;

    public P2QuantileEstimator(double quantile) {
        if (quantile <= 0.0 || quantile >= 1.0) {
            throw new IllegalArgumentException("Quantile must be in (0, 1)");
        }
        this.quantile = quantile;
    }

    public void add(double value) {
        if (count < 5) {
            initial[count++] = value;
            if (count == 5) {
                initializeMarkers();
            }
            return;
        }

        count++;

        int k;
        if (value < markerHeights[0]) {
            markerHeights[0] = value;
            k = 0;
        } else if (value < markerHeights[1]) {
            k = 0;
        } else if (value < markerHeights[2]) {
            k = 1;
        } else if (value < markerHeights[3]) {
            k = 2;
        } else if (value <= markerHeights[4]) {
            k = 3;
        } else {
            markerHeights[4] = value;
            k = 3;
        }

        for (int i = k + 1; i < 5; i++) {
            markerPositions[i]++;
        }

        for (int i = 0; i < 5; i++) {
            desiredPositions[i] += positionIncrements[i];
        }

        for (int i = 1; i <= 3; i++) {
            double delta = desiredPositions[i] - markerPositions[i];
            if ((delta >= 1.0 && markerPositions[i + 1] - markerPositions[i] > 1)
                    || (delta <= -1.0 && markerPositions[i - 1] - markerPositions[i] < -1)) {
                int direction = delta >= 0.0 ? 1 : -1;
                double candidate = parabolic(i, direction);
                if (candidate > markerHeights[i - 1] && candidate < markerHeights[i + 1]) {
                    markerHeights[i] = candidate;
                } else {
                    markerHeights[i] = linear(i, direction);
                }
                markerPositions[i] += direction;
            }
        }
    }

    public double getQuantile() {
        if (count == 0) {
            return 0.0;
        }

        if (count <= 5) {
            double[] snapshot = Arrays.copyOf(initial, count);
            Arrays.sort(snapshot);
            int index = (int) Math.ceil(quantile * snapshot.length) - 1;
            index = Math.max(0, Math.min(index, snapshot.length - 1));
            return snapshot[index];
        }

        return markerHeights[2];
    }

    private void initializeMarkers() {
        Arrays.sort(initial);
        System.arraycopy(initial, 0, markerHeights, 0, 5);

        markerPositions[0] = 1;
        markerPositions[1] = 2;
        markerPositions[2] = 3;
        markerPositions[3] = 4;
        markerPositions[4] = 5;

        desiredPositions[0] = 1;
        desiredPositions[1] = 1 + 2 * quantile;
        desiredPositions[2] = 1 + 4 * quantile;
        desiredPositions[3] = 3 + 2 * quantile;
        desiredPositions[4] = 5;

        positionIncrements[0] = 0;
        positionIncrements[1] = quantile / 2.0;
        positionIncrements[2] = quantile;
        positionIncrements[3] = (1 + quantile) / 2.0;
        positionIncrements[4] = 1;
    }

    private double parabolic(int i, int direction) {
        int n0 = markerPositions[i - 1];
        int n1 = markerPositions[i];
        int n2 = markerPositions[i + 1];

        double q0 = markerHeights[i - 1];
        double q1 = markerHeights[i];
        double q2 = markerHeights[i + 1];

        double left = (n1 - n0 + direction) * (q2 - q1) / (n2 - n1);
        double right = (n2 - n1 - direction) * (q1 - q0) / (n1 - n0);
        return q1 + direction * (left + right) / (n2 - n0);
    }

    private double linear(int i, int direction) {
        return markerHeights[i]
                + direction * (markerHeights[i + direction] - markerHeights[i])
                / (markerPositions[i + direction] - markerPositions[i]);
    }
}
