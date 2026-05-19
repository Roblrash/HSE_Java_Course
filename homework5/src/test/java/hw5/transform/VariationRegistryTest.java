package hw5.transform;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VariationRegistryTest {
    @Test
    void shouldResolveKnownVariation() {
        var registry = new VariationRegistry();
        assertNotNull(registry.getOrThrow("swirl"));
    }

    @Test
    void shouldThrowOnUnknownVariation() {
        var registry = new VariationRegistry();
        assertThrows(IllegalArgumentException.class, () -> registry.getOrThrow("unknown"));
    }
}
