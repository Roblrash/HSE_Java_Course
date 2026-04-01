package ru.tbank.loganalyzer;

import org.junit.jupiter.api.Test;
import ru.tbank.loganalyzer.service.P2QuantileEstimator;

import static org.assertj.core.api.Assertions.assertThat;

class P2QuantileEstimatorTest {
    @Test
    void keepsConstantValueForConstantStream() {
        P2QuantileEstimator estimator = new P2QuantileEstimator(0.95);
        for (int i = 0; i < 1000; i++) {
            estimator.add(42);
        }

        assertThat(estimator.getQuantile()).isEqualTo(42.0);
    }

    @Test
    void estimatesP95ForOrderedSequence() {
        P2QuantileEstimator estimator = new P2QuantileEstimator(0.95);
        for (int i = 1; i <= 100; i++) {
            estimator.add(i);
        }

        assertThat(estimator.getQuantile()).isBetween(90.0, 99.0);
    }
}
