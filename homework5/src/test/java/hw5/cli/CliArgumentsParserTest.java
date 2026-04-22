package hw5.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CliArgumentsParserTest {
    @Test
    void shouldParseArguments() {
        CliArgumentsParser parser = new CliArgumentsParser();
        String[] args = {
            "-w", "800",
            "-h", "600",
            "-i", "1000",
            "-o", "out.png",
            "-t", "4",
            "--seed", "42",
            "-f", "swirl:1.0,horseshoe:0.5",
            "-ap", "0.5,0,0,0,0.5,0/0.3,0,0,0,0.3,0",
            "-s", "3"
        };

        CliParseResult result = parser.parse(args);

        assertEquals(800, result.partialConfig().size().width());
        assertEquals(600, result.partialConfig().size().height());
        assertEquals(1000, result.partialConfig().iterationCount());
        assertEquals("out.png", result.partialConfig().outputPath());
        assertEquals(4, result.partialConfig().threads());
        assertEquals(42L, result.partialConfig().seed());
        assertEquals(2, result.partialConfig().functions().size());
        assertEquals(2, result.partialConfig().affineParams().size());
        assertEquals(3, result.partialConfig().symmetryLevel());
    }

    @Test
    void shouldFailOnMissingValue() {
        CliArgumentsParser parser = new CliArgumentsParser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"-w"}));
    }
}
