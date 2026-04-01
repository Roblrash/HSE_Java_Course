package ru.tbank.loganalyzer.parse;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NginxLogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+) - (\\S+) \\[([^\\]]+)] \"([^\"]*)\" (\\d{3}) (\\d+|-) \"([^\"]*)\" \"([^\"]*)\"$"
    );
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public Optional<LogEntry> parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String timeRaw = matcher.group(3);
        String requestRaw = matcher.group(4);
        String statusRaw = matcher.group(5);
        String sizeRaw = matcher.group(6);

        String resource = extractResource(requestRaw);
        if (resource == null) {
            return Optional.empty();
        }

        try {
            OffsetDateTime timestamp = OffsetDateTime.parse(timeRaw, TIME_FORMATTER);
            int status = Integer.parseInt(statusRaw);
            long size = "-".equals(sizeRaw) ? 0L : Long.parseLong(sizeRaw);
            return Optional.of(new LogEntry(timestamp, status, size, resource));
        } catch (DateTimeParseException | NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    private String extractResource(String requestRaw) {
        String[] parts = requestRaw.split(" ");
        if (parts.length < 2) {
            return null;
        }
        return parts[1];
    }
}
