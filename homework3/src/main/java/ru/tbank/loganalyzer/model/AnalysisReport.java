package ru.tbank.loganalyzer.model;

import java.util.List;

public record AnalysisReport(
        List<String> files,
        long totalRequestsCount,
        ResponseSizeStats responseSizeInBytes,
        List<ResourceStat> resources,
        List<ResponseCodeStat> responseCodes,
        List<RequestPerDateStat> requestsPerDate,
        String analyzedFrom,
        String analyzedTo
) {
}
