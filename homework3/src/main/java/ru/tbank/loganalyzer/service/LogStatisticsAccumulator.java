package ru.tbank.loganalyzer.service;

import ru.tbank.loganalyzer.model.AnalysisReport;
import ru.tbank.loganalyzer.model.RequestPerDateStat;
import ru.tbank.loganalyzer.model.ResourceStat;
import ru.tbank.loganalyzer.model.ResponseCodeStat;
import ru.tbank.loganalyzer.model.ResponseSizeStats;
import ru.tbank.loganalyzer.parse.LogEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LogStatisticsAccumulator {
    private long totalRequests;
    private long totalResponseBytes;
    private long maxResponseBytes;

    private final P2QuantileEstimator p95Estimator = new P2QuantileEstimator(0.95);
    private final Map<Integer, Long> responseCodes = new HashMap<>();
    private final Map<String, Long> resources = new HashMap<>();
    private final Map<LocalDate, Long> requestsPerDate = new HashMap<>();

    private LocalDate minDate;
    private LocalDate maxDate;

    public void add(LogEntry entry) {
        totalRequests++;
        totalResponseBytes += entry.responseSizeBytes();
        maxResponseBytes = Math.max(maxResponseBytes, entry.responseSizeBytes());
        p95Estimator.add(entry.responseSizeBytes());

        responseCodes.merge(entry.statusCode(), 1L, Long::sum);
        resources.merge(entry.resource(), 1L, Long::sum);

        LocalDate date = entry.timestamp().toLocalDate();
        requestsPerDate.merge(date, 1L, Long::sum);

        if (minDate == null || date.isBefore(minDate)) {
            minDate = date;
        }
        if (maxDate == null || date.isAfter(maxDate)) {
            maxDate = date;
        }
    }

    public AnalysisReport toReport(List<String> files) {
        double average = totalRequests == 0 ? 0.0 : PercentileCalculator.round2((double) totalResponseBytes / totalRequests);
        double max = PercentileCalculator.round2(maxResponseBytes);
        double p95 = PercentileCalculator.round2(p95Estimator.getQuantile());

        List<ResourceStat> topResources = resources.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(10)
                .map(entry -> new ResourceStat(entry.getKey(), entry.getValue()))
                .toList();

        List<ResponseCodeStat> codeStats = responseCodes.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(entry -> new ResponseCodeStat(entry.getKey(), entry.getValue()))
                .toList();

        List<RequestPerDateStat> dateStats = requestsPerDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new RequestPerDateStat(
                        entry.getKey().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        entry.getValue(),
                        totalRequests == 0 ? 0.0 : PercentileCalculator.round2((entry.getValue() * 100.0) / totalRequests)
                ))
                .toList();

        return new AnalysisReport(
                files,
                totalRequests,
                new ResponseSizeStats(average, max, p95),
                topResources,
                codeStats,
                dateStats,
                minDate == null ? null : minDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                maxDate == null ? null : maxDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }
}
