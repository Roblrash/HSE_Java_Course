package hw5.transform;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VariationRegistry {
    private final Map<String, Variation> variations = new ConcurrentHashMap<>();

    public VariationRegistry() {
        register(new LinearVariation());
        register(new SinusoidalVariation());
        register(new SphericalVariation());
        register(new SwirlVariation());
        register(new HorseshoeVariation());
        register(new PolarVariation());
    }

    public void register(Variation variation) {
        variations.put(variation.name().toLowerCase(Locale.ROOT), variation);
    }

    public Variation getOrThrow(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Variation name is null");
        }
        Variation variation = variations.get(name.toLowerCase(Locale.ROOT));
        if (variation == null) {
            throw new IllegalArgumentException("Unknown variation: " + name + ". Available: " + variations.keySet());
        }
        return variation;
    }
}

