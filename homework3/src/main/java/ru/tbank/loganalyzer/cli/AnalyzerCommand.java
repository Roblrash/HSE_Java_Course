package ru.tbank.loganalyzer.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import ru.tbank.loganalyzer.model.AnalysisReport;
import ru.tbank.loganalyzer.parse.NginxLogParser;
import ru.tbank.loganalyzer.render.JsonReportRenderer;
import ru.tbank.loganalyzer.render.MarkdownReportRenderer;
import ru.tbank.loganalyzer.render.ReportRenderer;
import ru.tbank.loganalyzer.service.LogAnalyzerService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@CommandLine.Command(
        name = "log-analyzer",
        mixinStandardHelpOptions = true,
        description = "Analyze NGINX logs and write report"
)
public class AnalyzerCommand implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerCommand.class);
    private static final Pattern GLOB_SYMBOLS = Pattern.compile("[\\*\\?\\[\\]\\{\\}]");

    @CommandLine.Option(names = {"--path", "-p"}, required = true, split = ",", arity = "1..*",
            description = "Path to NGINX log file(s)")
    private List<String> paths;

    @CommandLine.Option(names = {"--format", "-f"}, required = true,
            description = "Output format: json or markdown")
    private String format;

    @CommandLine.Option(names = {"--output", "-o"}, required = true,
            description = "Output file path")
    private String output;

    @CommandLine.Option(names = "--from", description = "From date/time in ISO8601")
    private String from;

    @CommandLine.Option(names = "--to", description = "To date/time in ISO8601")
    private String to;

    @Override
    public Integer call() {
        OutputFormat outputFormat = OutputFormat.fromCliValue(format);
        Path outputPath = Paths.get(output);

        validateOutputPath(outputPath, outputFormat);

        ParsedDate fromParsed = parseIsoDateOrDateTime(from, "--from", false);
        ParsedDate toParsed = parseIsoDateOrDateTime(to, "--to", true);

        if (fromParsed != null && toParsed != null && !fromParsed.comparableInstant().isBefore(toParsed.comparableInstant())) {
            throw new InvalidUsageException("Invalid date range: --from must be less than --to");
        }

        List<Path> inputFiles = resolveAndValidateInputFiles(paths);
        LOGGER.info("Resolved {} input file(s)", inputFiles.size());

        LogAnalyzerService service = new LogAnalyzerService(new NginxLogParser());
        AnalysisReport report = service.analyze(
                inputFiles,
                fromParsed == null ? null : fromParsed.filterInstant(),
                toParsed == null ? null : toParsed.filterInstant()
        );

        ReportRenderer renderer = outputFormat == OutputFormat.JSON
                ? new JsonReportRenderer()
                : new MarkdownReportRenderer();

        String rendered = renderer.render(report);
        writeOutput(outputPath, rendered);

        return 0;
    }

    private List<Path> resolveAndValidateInputFiles(List<String> rawPaths) {
        Set<Path> resolved = new LinkedHashSet<>();

        for (String rawPath : rawPaths) {
            if (GLOB_SYMBOLS.matcher(rawPath).find()) {
                resolved.addAll(resolveGlob(rawPath));
            } else {
                try {
                    resolved.add(Paths.get(rawPath));
                } catch (InvalidPathException e) {
                    throw new InvalidUsageException("Invalid input path: " + rawPath);
                }
            }
        }

        if (resolved.isEmpty()) {
            throw new InvalidUsageException("No files matched by --path");
        }

        List<Path> files = new ArrayList<>(resolved);
        for (Path file : files) {
            if (!Files.exists(file)) {
                throw new InvalidUsageException("File not found: " + file);
            }
            if (!Files.isRegularFile(file)) {
                throw new InvalidUsageException("Path is not a file: " + file);
            }
            String lower = file.getFileName().toString().toLowerCase();
            if (!(lower.endsWith(".log") || lower.endsWith(".txt"))) {
                throw new InvalidUsageException("Unsupported input file extension for file: " + file + ". Supported: .log, .txt");
            }
        }

        return files;
    }

    private List<Path> resolveGlob(String rawPath) {
        int wildcardIndex = firstWildcardIndex(rawPath);
        if (wildcardIndex < 0) {
            return List.of(Paths.get(rawPath));
        }

        int slashIndexForward = rawPath.lastIndexOf('/', wildcardIndex);
        int slashIndexBackward = rawPath.lastIndexOf('\\', wildcardIndex);
        int splitIndex = Math.max(slashIndexForward, slashIndexBackward);

        String parentPart = splitIndex >= 0 ? rawPath.substring(0, splitIndex) : ".";
        String pattern = splitIndex >= 0 ? rawPath.substring(splitIndex + 1) : rawPath;

        if (pattern.isBlank()) {
            throw new InvalidUsageException("Invalid glob path: " + rawPath);
        }

        Path parent;
        try {
            parent = Paths.get(parentPart.isBlank() ? "." : parentPart);
        } catch (InvalidPathException e) {
            throw new InvalidUsageException("Invalid glob path: " + rawPath);
        }

        List<Path> matched = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent, pattern)) {
            for (Path path : stream) {
                matched.add(path);
            }
        } catch (IOException e) {
            throw new InvalidUsageException("Failed to resolve glob path: " + rawPath);
        }

        return matched;
    }

    private int firstWildcardIndex(String value) {
        int star = value.indexOf('*');
        int q = value.indexOf('?');
        int open = value.indexOf('[');
        int close = value.indexOf(']');
        int braceOpen = value.indexOf('{');
        int braceClose = value.indexOf('}');

        int min = Integer.MAX_VALUE;
        for (int index : new int[]{star, q, open, close, braceOpen, braceClose}) {
            if (index >= 0) {
                min = Math.min(min, index);
            }
        }
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private void validateOutputPath(Path outputPath, OutputFormat formatType) {
        if (Files.exists(outputPath)) {
            throw new InvalidUsageException("Output file already exists: " + outputPath);
        }

        Path parent = outputPath.toAbsolutePath().getParent();
        if (parent == null || !Files.exists(parent)) {
            throw new InvalidUsageException("Output directory does not exist: " + parent);
        }
        if (!Files.isWritable(parent)) {
            throw new InvalidUsageException("Output directory is not writable: " + parent);
        }

        String lower = outputPath.getFileName().toString().toLowerCase();
        if (!lower.endsWith(formatType.extension())) {
            throw new InvalidUsageException("Invalid output file extension. Expected: " + formatType.extension());
        }
    }

    private ParsedDate parseIsoDateOrDateTime(String value, String optionName, boolean endOfDayForDate) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            Instant instant = Instant.parse(value);
            return new ParsedDate(instant, instant);
        } catch (DateTimeParseException ignored) {
        }

        try {
            Instant instant = OffsetDateTime.parse(value).toInstant();
            return new ParsedDate(instant, instant);
        } catch (DateTimeParseException ignored) {
        }

        try {
            LocalDate localDate = LocalDate.parse(value);
            Instant comparable = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant filter = endOfDayForDate
                    ? localDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusNanos(1)
                    : comparable;
            return new ParsedDate(filter, comparable);
        } catch (DateTimeParseException ignored) {
        }

        throw new InvalidUsageException("Invalid ISO8601 date in " + optionName + ": " + value);
    }

    private void writeOutput(Path outputPath, String content) {
        try {
            Files.writeString(outputPath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write output: " + outputPath, e);
        }
    }

    private record ParsedDate(Instant filterInstant, Instant comparableInstant) {
    }
}
