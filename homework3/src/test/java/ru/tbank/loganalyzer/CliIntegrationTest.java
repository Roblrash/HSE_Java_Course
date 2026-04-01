package ru.tbank.loganalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import ru.tbank.loganalyzer.cli.AnalyzerCommand;
import ru.tbank.loganalyzer.cli.InvalidUsageException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CliIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void returnsTwoForInvalidOutputExtension() throws Exception {
        Path logFile = tempDir.resolve("access.log");
        Files.writeString(logFile,
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /a HTTP/1.1\" 200 10 \"-\" \"UA\"\n",
                StandardCharsets.UTF_8);

        CommandLine commandLine = configuredCommandLine();
        int exitCode = commandLine.execute(
                "--path", logFile.toString(),
                "--format", "json",
                "--output", tempDir.resolve("report.md").toString()
        );

        assertThat(exitCode).isEqualTo(2);
    }

    @Test
    void returnsTwoWhenFromEqualsTo() throws Exception {
        Path logFile = tempDir.resolve("access.log");
        Files.writeString(logFile,
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /a HTTP/1.1\" 200 10 \"-\" \"UA\"\n",
                StandardCharsets.UTF_8);

        CommandLine commandLine = configuredCommandLine();
        int exitCode = commandLine.execute(
                "--path", logFile.toString(),
                "--format", "json",
                "--output", tempDir.resolve("report.json").toString(),
                "--from", "2025-03-01",
                "--to", "2025-03-01"
        );

        assertThat(exitCode).isEqualTo(2);
    }

    @Test
    void generatesJsonReport() throws Exception {
        Path logFile = tempDir.resolve("access.log");
        Path outFile = tempDir.resolve("report.json");

        Files.writeString(logFile,
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /a HTTP/1.1\" 200 10 \"-\" \"UA\"\n",
                StandardCharsets.UTF_8);

        CommandLine commandLine = configuredCommandLine();
        int exitCode = commandLine.execute(
                "--path", logFile.toString(),
                "--format", "json",
                "--output", outFile.toString()
        );

        assertThat(exitCode).isEqualTo(0);
        assertThat(Files.exists(outFile)).isTrue();
        assertThat(Files.readString(outFile)).contains("\"totalRequestsCount\" : 1");
    }

    private CommandLine configuredCommandLine() {
        CommandLine commandLine = new CommandLine(new AnalyzerCommand());
        commandLine.setExecutionExceptionHandler((exception, commandLine1, parseResult) -> {
            if (exception instanceof InvalidUsageException) {
                return 2;
            }
            return 1;
        });
        return commandLine;
    }
}
