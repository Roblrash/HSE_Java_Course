package ru.tbank.loganalyzer;

import org.junit.jupiter.api.Test;
import ru.tbank.loganalyzer.parse.NginxLogParser;

import static org.assertj.core.api.Assertions.assertThat;

class NginxLogParserTest {
    private final NginxLogParser parser = new NginxLogParser();

    @Test
    void parsesValidNginxLine() {
        String line = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3\"";

        var parsed = parser.parse(line);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().statusCode()).isEqualTo(304);
        assertThat(parsed.get().resource()).isEqualTo("/downloads/product_1");
        assertThat(parsed.get().responseSizeBytes()).isZero();
    }

    @Test
    void returnsEmptyForMalformedLine() {
        String line = "this is not nginx log format";

        var parsed = parser.parse(line);

        assertThat(parsed).isEmpty();
    }
}
