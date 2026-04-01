package ru.tbank.loganalyzer.render;

import ru.tbank.loganalyzer.model.AnalysisReport;
import ru.tbank.loganalyzer.model.RequestPerDateStat;
import ru.tbank.loganalyzer.model.ResourceStat;
import ru.tbank.loganalyzer.model.ResponseCodeStat;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class MarkdownReportRenderer implements ReportRenderer {
    private static final Map<Integer, String> STATUS_NAMES = statusNames();

    @Override
    public String render(AnalysisReport report) {
        StringBuilder builder = new StringBuilder();

        builder.append("## General Information\n");
        builder.append("| Metric | Value |\n");
        builder.append("|:--|--:|\n");
        builder.append("| File(s) | ").append(joinFiles(report)).append(" |\n");
        builder.append("| Start date | ").append(report.analyzedFrom() == null ? "-" : report.analyzedFrom()).append(" |\n");
        builder.append("| End date | ").append(report.analyzedTo() == null ? "-" : report.analyzedTo()).append(" |\n");
        builder.append("| Total requests | ").append(report.totalRequestsCount()).append(" |\n");
        builder.append("| Average response size | ").append(formatBytes(report.responseSizeInBytes().average())).append(" |\n");
        builder.append("| Max response size | ").append(formatBytes(report.responseSizeInBytes().max())).append(" |\n");
        builder.append("| P95 response size | ").append(formatBytes(report.responseSizeInBytes().p95())).append(" |\n\n");

        builder.append("## Requested Resources\n");
        builder.append("| Resource | Count |\n");
        builder.append("|:--|--:|\n");
        for (ResourceStat resource : report.resources()) {
            builder.append("| ").append(resource.resource()).append(" | ").append(resource.totalRequestsCount()).append(" |\n");
        }
        builder.append("\n");

        builder.append("## Response Codes\n");
        builder.append("| Code | Name | Count |\n");
        builder.append("|--:|:--|--:|\n");
        for (ResponseCodeStat code : report.responseCodes()) {
            builder.append("| ").append(code.code())
                    .append(" | ").append(STATUS_NAMES.getOrDefault(code.code(), "Unknown"))
                    .append(" | ").append(code.totalResponsesCount())
                    .append(" |\n");
        }

        if (!report.requestsPerDate().isEmpty()) {
            builder.append("\n## Requests Per Date\n");
            builder.append("| Date | Weekday | Count | Share, % |\n");
            builder.append("|:--|:--|--:|--:|\n");
            for (RequestPerDateStat stat : report.requestsPerDate()) {
                builder.append("| ").append(stat.date())
                        .append(" | ").append(stat.weekday())
                        .append(" | ").append(stat.totalRequestsCount())
                        .append(" | ").append(String.format("%.2f", stat.totalRequestsPercentage()))
                        .append(" |\n");
            }
        }

        return builder.toString();
    }

    private String formatBytes(double value) {
        return String.format("%.2fb", value);
    }

    private String joinFiles(AnalysisReport report) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String file : report.files()) {
            joiner.add(file);
        }
        return joiner.toString();
    }

    private static Map<Integer, String> statusNames() {
        Map<Integer, String> statusNames = new HashMap<>();
        statusNames.put(200, "OK");
        statusNames.put(201, "Created");
        statusNames.put(204, "No Content");
        statusNames.put(301, "Moved Permanently");
        statusNames.put(302, "Found");
        statusNames.put(304, "Not Modified");
        statusNames.put(400, "Bad Request");
        statusNames.put(401, "Unauthorized");
        statusNames.put(403, "Forbidden");
        statusNames.put(404, "Not Found");
        statusNames.put(429, "Too Many Requests");
        statusNames.put(500, "Internal Server Error");
        statusNames.put(502, "Bad Gateway");
        statusNames.put(503, "Service Unavailable");
        statusNames.put(504, "Gateway Timeout");
        return statusNames;
    }
}
