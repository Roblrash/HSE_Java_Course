package ru.tbank.loganalyzer.model;

public record RequestPerDateStat(
        String date,
        String weekday,
        long totalRequestsCount,
        double totalRequestsPercentage
) {
}
