package hw5.config;

import java.util.ArrayList;
import java.util.List;

public final class ConfigValidator {
    private ConfigValidator() {
    }

    public static void validateOrThrow(GenerationConfig config) {
        List<String> errors = new ArrayList<>();

        if (config.size().width() <= 0) {
            errors.add("width must be > 0");
        }
        if (config.size().height() <= 0) {
            errors.add("height must be > 0");
        }
        if (config.iterationCount() <= 0) {
            errors.add("iteration-count must be > 0");
        }
        if (config.threads() <= 0) {
            errors.add("threads must be > 0");
        }
        if (config.functions().isEmpty()) {
            errors.add("functions must not be empty");
        }
        if (config.affineParams().isEmpty()) {
            errors.add("affine-params must not be empty");
        }
        if (config.outputPath() == null || config.outputPath().isBlank()) {
            errors.add("output-path must not be blank");
        }
        if (config.symmetryLevel() < 1) {
            errors.add("symmetry-level must be >= 1");
        }
        if (config.warmupIterations() < 0) {
            errors.add("warmup-iterations must be >= 0");
        }
        if (!Double.isFinite(config.gamma()) || config.gamma() <= 0.0) {
            errors.add("gamma must be finite and > 0");
        }

        double sumWeights = 0.0;
        for (int i = 0; i < config.functions().size(); i++) {
            var spec = config.functions().get(i);
            if (spec.name() == null || spec.name().isBlank()) {
                errors.add("functions[" + i + "].name must not be blank");
            }
            if (!Double.isFinite(spec.weight()) || spec.weight() <= 0.0) {
                errors.add("functions[" + i + "].weight must be finite and > 0");
            }
            sumWeights += spec.weight();
        }
        if (!Double.isFinite(sumWeights) || sumWeights <= 0.0) {
            errors.add("sum of function weights must be > 0");
        }

        for (int i = 0; i < config.affineParams().size(); i++) {
            var a = config.affineParams().get(i);
            if (!Double.isFinite(a.a()) || !Double.isFinite(a.b()) || !Double.isFinite(a.c())
                || !Double.isFinite(a.d()) || !Double.isFinite(a.e()) || !Double.isFinite(a.f())) {
                errors.add("affine-params[" + i + "] contains non-finite value");
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }
}
