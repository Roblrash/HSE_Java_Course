package hw5.cli;

import hw5.config.PartialConfig;

public record CliParseResult(PartialConfig partialConfig, String configPath) {
}
