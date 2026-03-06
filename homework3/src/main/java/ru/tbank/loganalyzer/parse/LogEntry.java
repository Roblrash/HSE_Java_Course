package ru.tbank.loganalyzer.parse;

import java.time.OffsetDateTime;

public record LogEntry(OffsetDateTime timestamp, int statusCode, long responseSizeBytes, String resource) {
}
