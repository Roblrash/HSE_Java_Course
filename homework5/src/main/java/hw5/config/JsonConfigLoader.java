package hw5.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import hw5.model.AffineTransform;
import hw5.model.FunctionSpec;
import hw5.model.Size;

public final class JsonConfigLoader {
    private final ObjectMapper mapper;

    public JsonConfigLoader() {
        this.mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public PartialConfig load(Path path) throws IOException {
        JsonConfig json = mapper.readValue(path.toFile(), JsonConfig.class);

        Size size = null;
        if (json.size() != null && json.size().width() != null && json.size().height() != null) {
            size = new Size(json.size().width(), json.size().height());
        }

        Long seed = null;
        if (json.seed() != null) {
            seed = (long) (double) json.seed();
        }

        List<FunctionSpec> functions = null;
        if (json.functions() != null) {
            functions = new ArrayList<>();
            for (JsonConfig.FunctionJson f : json.functions()) {
                functions.add(new FunctionSpec(
                    f.name(),
                    f.weight() == null ? 1.0 : f.weight(),
                    f.params() == null ? Map.of() : f.params()
                ));
            }
        }

        List<AffineTransform> affineParams = null;
        if (json.affineParams() != null) {
            affineParams = new ArrayList<>();
            for (JsonConfig.AffineJson a : json.affineParams()) {
                affineParams.add(new AffineTransform(
                    nvl(a.a()), nvl(a.b()), nvl(a.c()), nvl(a.d()), nvl(a.e()), nvl(a.f())
                ));
            }
        }

        return new PartialConfig(
            size,
            json.iterationCount(),
            json.outputPath(),
            json.threads(),
            seed,
            functions,
            affineParams,
            json.symmetryLevel(),
            json.warmupIterations(),
            json.gamma()
        );
    }

    private static double nvl(Double value) {
        return value == null ? 0.0 : value;
    }
}
