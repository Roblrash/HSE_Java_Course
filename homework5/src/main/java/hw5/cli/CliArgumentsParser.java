package hw5.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import hw5.config.PartialConfig;
import hw5.model.AffineTransform;
import hw5.model.FunctionSpec;
import hw5.model.Size;

public final class CliArgumentsParser {

    public CliParseResult parse(String[] args) {
        Map<String, String> kv = parseMap(args);

        Size size = null;
        Integer width = parseInt(kv.get("--width"), kv.get("-w"));
        Integer height = parseInt(kv.get("--height"), kv.get("-h"));
        if (width != null || height != null) {
            size = new Size(requireNonNull(width, "width"), requireNonNull(height, "height"));
        }

        Integer iterationCount = parseInt(kv.get("--iteration-count"), kv.get("-i"));
        String outputPath = first(kv.get("--output-path"), kv.get("-o"));
        Integer threads = parseInt(kv.get("--threads"), kv.get("-t"));
        Long seed = parseLong(kv.get("--seed"));
        Integer symmetryLevel = parseInt(kv.get("--symmetry-level"), kv.get("-s"));
        Integer warmupIterations = parseInt(kv.get("--warmup-iterations"));
        Double gamma = parseDouble(kv.get("--gamma"));

        List<AffineTransform> affineParams = null;
        String affineRaw = first(kv.get("--affine-params"), kv.get("-ap"));
        if (affineRaw != null) {
            affineParams = parseAffineParams(affineRaw);
        }

        List<FunctionSpec> functions = null;
        String functionsRaw = first(kv.get("--functions"), kv.get("-f"));
        if (functionsRaw != null) {
            functions = parseFunctions(functionsRaw);
        }

        PartialConfig partial = new PartialConfig(
            size,
            iterationCount,
            outputPath,
            threads,
            seed,
            functions,
            affineParams,
            symmetryLevel,
            warmupIterations,
            gamma
        );

        return new CliParseResult(partial, kv.get("--config"));
    }

    private static Map<String, String> parseMap(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            if (!key.startsWith("-")) {
                throw new IllegalArgumentException("Unexpected token: " + key);
            }
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for argument: " + key);
            }
            map.put(key, args[++i]);
        }
        return map;
    }

    private static List<AffineTransform> parseAffineParams(String raw) {
        String[] chunks = raw.split("/");
        List<AffineTransform> result = new ArrayList<>();
        for (String chunk : chunks) {
            String[] vals = chunk.split(",");
            if (vals.length != 6) {
                throw new IllegalArgumentException("Each affine transform must have 6 values: " + chunk);
            }
            result.add(new AffineTransform(
                parseDoubleStrict(vals[0], "a"),
                parseDoubleStrict(vals[1], "b"),
                parseDoubleStrict(vals[2], "c"),
                parseDoubleStrict(vals[3], "d"),
                parseDoubleStrict(vals[4], "e"),
                parseDoubleStrict(vals[5], "f")
            ));
        }
        return result;
    }

    private static List<FunctionSpec> parseFunctions(String raw) {
        String[] chunks = raw.split(",");
        List<FunctionSpec> result = new ArrayList<>();
        for (String chunk : chunks) {
            String[] parts = chunk.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Function must be in format name:weight: " + chunk);
            }
            String name = parts[0].trim();
            double weight = parseDoubleStrict(parts[1], "weight");
            result.add(new FunctionSpec(name, weight));
        }
        return result;
    }

    private static String first(String a, String b) {
        return a != null ? a : b;
    }

    private static Integer requireNonNull(Integer value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("Missing required pair argument: " + name);
        }
        return value;
    }

    private static Integer parseInt(String value) {
        return parseInt(value, null);
    }

    private static Integer parseInt(String value, String fallback) {
        String v = first(value, fallback);
        if (v == null) {
            return null;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid int value: " + v, ex);
        }
    }

    private static Long parseLong(String value) {
        if (value == null) {
            return null;
        }
        try {
            if (value.contains(".")) {
                return (long) Double.parseDouble(value);
            }
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid long value: " + value, ex);
        }
    }

    private static Double parseDouble(String value) {
        if (value == null) {
            return null;
        }
        return parseDoubleStrict(value, "value");
    }

    private static double parseDoubleStrict(String value, String name) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid double for " + name + ": " + value, ex);
        }
    }
}
