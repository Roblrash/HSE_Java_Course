package ru.tbank.loganalyzer.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentileCalculator {
    private PercentileCalculator() {
    }

    public static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
