package ru.tbank.loganalyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tbank.loganalyzer.model.AnalysisReport;
import ru.tbank.loganalyzer.parse.LogEntry;
import ru.tbank.loganalyzer.parse.NginxLogParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class LogAnalyzerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnalyzerService.class);

    private final NginxLogParser parser;

    public LogAnalyzerService(NginxLogParser parser) {
        this.parser = parser;
    }

    public AnalysisReport analyze(List<Path> files, Instant from, Instant to) {
        LOGGER.info("Start analyzing {} file(s)", files.size());
        LogStatisticsAccumulator accumulator = new LogStatisticsAccumulator();

        for (Path file : files) {
            analyzeFile(file, from, to, accumulator);
        }

        AnalysisReport report = accumulator.toReport(files.stream().map(Path::toString).toList());
        LOGGER.info("Analysis finished. Total requests: {}", report.totalRequestsCount());
        return report;
    }

    private void analyzeFile(Path file, Instant from, Instant to, LogStatisticsAccumulator accumulator) {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Optional<LogEntry> parsed = parser.parse(line);
                if (parsed.isEmpty()) {
                    LOGGER.warn("Skipping malformed line in {}: {}", file, line);
                    continue;
                }

                LogEntry entry = parsed.get();
                Instant timestamp = entry.timestamp().toInstant();
                if (from != null && timestamp.isBefore(from)) {
                    continue;
                }
                if (to != null && timestamp.isAfter(to)) {
                    continue;
                }

                accumulator.add(entry);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + file, e);
        }
    }
}
