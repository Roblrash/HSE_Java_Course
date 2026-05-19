package hw5;

import hw5.cli.CliArgumentsParser;
import hw5.config.ConfigResolver;
import hw5.config.GenerationConfig;
import hw5.config.JsonConfigLoader;
import hw5.io.PngWriter;
import hw5.render.FlameRenderer;
import hw5.transform.VariationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        try {
            CliArgumentsParser parser = new CliArgumentsParser();
            ConfigResolver resolver = new ConfigResolver(new JsonConfigLoader());
            GenerationConfig config = resolver.resolve(parser.parse(args));

            FlameRenderer renderer = new FlameRenderer(new VariationRegistry());
            var rendered = renderer.render(config);

            new PngWriter().write(rendered.image(), config.outputPath());
            log.info("Image saved to {}", config.outputPath());
        } catch (Exception ex) {
            System.err.println("Application error: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
