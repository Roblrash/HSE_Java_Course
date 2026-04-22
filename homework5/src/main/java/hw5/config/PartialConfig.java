package hw5.config;

import java.util.List;
import hw5.model.AffineTransform;
import hw5.model.FunctionSpec;
import hw5.model.Size;

public record PartialConfig(
    Size size,
    Integer iterationCount,
    String outputPath,
    Integer threads,
    Long seed,
    List<FunctionSpec> functions,
    List<AffineTransform> affineParams,
    Integer symmetryLevel,
    Integer warmupIterations,
    Double gamma
) {
    public static PartialConfig empty() {
        return new PartialConfig(null, null, null, null, null, null, null, null, null, null);
    }
}
