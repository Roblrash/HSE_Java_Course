package hw5.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import hw5.model.AffineTransform;
import hw5.model.FunctionSpec;
import hw5.model.Size;

public final class GenerationConfig {
    public static final int DEFAULT_WIDTH = 1920;
    public static final int DEFAULT_HEIGHT = 1080;
    public static final long DEFAULT_SEED = 5L;
    public static final int DEFAULT_ITERATION_COUNT = 2_500;
    public static final String DEFAULT_OUTPUT_PATH = "result.png";
    public static final int DEFAULT_THREADS = 1;
    public static final int DEFAULT_SYMMETRY_LEVEL = 1;
    public static final int DEFAULT_WARMUP_ITERATIONS = 20;
    public static final double DEFAULT_GAMMA = 2.2;

    private final Size size;
    private final int iterationCount;
    private final String outputPath;
    private final int threads;
    private final long seed;
    private final List<FunctionSpec> functions;
    private final List<AffineTransform> affineParams;
    private final int symmetryLevel;
    private final int warmupIterations;
    private final double gamma;

    public GenerationConfig(
        Size size,
        int iterationCount,
        String outputPath,
        int threads,
        long seed,
        List<FunctionSpec> functions,
        List<AffineTransform> affineParams,
        int symmetryLevel,
        int warmupIterations,
        double gamma
    ) {
        this.size = size;
        this.iterationCount = iterationCount;
        this.outputPath = outputPath;
        this.threads = threads;
        this.seed = seed;
        this.functions = List.copyOf(functions);
        this.affineParams = List.copyOf(affineParams);
        this.symmetryLevel = symmetryLevel;
        this.warmupIterations = warmupIterations;
        this.gamma = gamma;
    }

    public static GenerationConfig defaults() {
        return new GenerationConfig(
            new Size(DEFAULT_WIDTH, DEFAULT_HEIGHT),
            DEFAULT_ITERATION_COUNT,
            DEFAULT_OUTPUT_PATH,
            DEFAULT_THREADS,
            DEFAULT_SEED,
            List.of(
                new FunctionSpec("linear", 1.0, Map.of()),
                new FunctionSpec("swirl", 1.0, Map.of()),
                new FunctionSpec("horseshoe", 1.0, Map.of()),
                new FunctionSpec("spherical", 1.0, Map.of()),
                new FunctionSpec("sinusoidal", 1.0, Map.of())
            ),
            List.of(
                new AffineTransform(0.50, 0.00, 0.00, 0.00, 0.50, 0.00),
                new AffineTransform(0.43, -0.31, 0.20, 0.31, 0.43, -0.17),
                new AffineTransform(0.46, 0.23, -0.21, -0.23, 0.46, 0.13),
                new AffineTransform(0.57, 0.07, -0.35, -0.07, 0.57, 0.32)
            ),
            DEFAULT_SYMMETRY_LEVEL,
            DEFAULT_WARMUP_ITERATIONS,
            DEFAULT_GAMMA
        );
    }

    public GenerationConfig withOverrides(PartialConfig override) {
        return new GenerationConfig(
            override.size() != null ? override.size() : size,
            override.iterationCount() != null ? override.iterationCount() : iterationCount,
            override.outputPath() != null ? override.outputPath() : outputPath,
            override.threads() != null ? override.threads() : threads,
            override.seed() != null ? override.seed() : seed,
            override.functions() != null ? new ArrayList<>(override.functions()) : functions,
            override.affineParams() != null ? new ArrayList<>(override.affineParams()) : affineParams,
            override.symmetryLevel() != null ? override.symmetryLevel() : symmetryLevel,
            override.warmupIterations() != null ? override.warmupIterations() : warmupIterations,
            override.gamma() != null ? override.gamma() : gamma
        );
    }

    public Size size() { return size; }
    public int iterationCount() { return iterationCount; }
    public String outputPath() { return outputPath; }
    public int threads() { return threads; }
    public long seed() { return seed; }
    public List<FunctionSpec> functions() { return functions; }
    public List<AffineTransform> affineParams() { return affineParams; }
    public int symmetryLevel() { return symmetryLevel; }
    public int warmupIterations() { return warmupIterations; }
    public double gamma() { return gamma; }
}
