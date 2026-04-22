package hw5.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record JsonConfig(
    SizeJson size,
    @JsonProperty("iteration_count") Integer iterationCount,
    @JsonProperty("output_path") String outputPath,
    Integer threads,
    Double seed,
    List<FunctionJson> functions,
    @JsonProperty("affine_params") List<AffineJson> affineParams,
    @JsonProperty("symmetry_level") Integer symmetryLevel,
    @JsonProperty("warmup_iterations") Integer warmupIterations,
    Double gamma
) {
    public record SizeJson(Integer width, Integer height) {}

    public record FunctionJson(String name, Double weight, Map<String, Double> params) {}

    public record AffineJson(Double a, Double b, Double c, Double d, Double e, Double f) {}
}
