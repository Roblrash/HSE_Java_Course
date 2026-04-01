package ru.tbank.loganalyzer;

import org.junit.jupiter.api.Test;
import ru.tbank.loganalyzer.model.AnalysisReport;
import ru.tbank.loganalyzer.parse.NginxLogParser;
import ru.tbank.loganalyzer.service.LogAnalyzerService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogAnalyzerServiceTest {
    @Test
    void analyzesStreamAndComputesStats() throws Exception {
        Path tempFile = Files.createTempFile("log-analyzer-test", ".log");
        Files.writeString(tempFile, String.join("\n",
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 100 \"-\" \"UA\"",
                "93.180.71.3 - - [17/May/2015:08:05:33 +0000] \"GET /downloads/product_1 HTTP/1.1\" 404 300 \"-\" \"UA\"",
                "93.180.71.3 - - [18/May/2015:08:05:33 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 500 \"-\" \"UA\"",
                "broken line"
        ), StandardCharsets.UTF_8);

        LogAnalyzerService service = new LogAnalyzerService(new NginxLogParser());
        AnalysisReport report = service.analyze(
                List.of(tempFile),
                Instant.parse("2015-05-17T00:00:00Z"),
                Instant.parse("2015-05-18T23:59:59Z")
        );

        assertThat(report.totalRequestsCount()).isEqualTo(3);
        assertThat(report.responseSizeInBytes().average()).isEqualTo(300.0);
        assertThat(report.responseSizeInBytes().max()).isEqualTo(500.0);
        assertThat(report.responseCodes()).hasSize(2);
        assertThat(report.resources().get(0).resource()).isEqualTo("/downloads/product_1");

        Files.deleteIfExists(tempFile);
    }
}
