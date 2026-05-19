package hw5.config;

import java.io.IOException;
import java.nio.file.Path;
import hw5.cli.CliParseResult;

public final class ConfigResolver {
    private final JsonConfigLoader jsonConfigLoader;

    public ConfigResolver(JsonConfigLoader jsonConfigLoader) {
        this.jsonConfigLoader = jsonConfigLoader;
    }

    public GenerationConfig resolve(CliParseResult cli) throws IOException {
        GenerationConfig config = GenerationConfig.defaults();

        if (cli.configPath() != null) {
            PartialConfig jsonPart = jsonConfigLoader.load(Path.of(cli.configPath()));
            config = config.withOverrides(jsonPart);
        }

        config = config.withOverrides(cli.partialConfig());
        ConfigValidator.validateOrThrow(config);
        return config;
    }
}
