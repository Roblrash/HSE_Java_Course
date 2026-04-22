package hw5.render;

import static org.junit.jupiter.api.Assertions.assertTrue;

import hw5.config.GenerationConfig;
import hw5.config.PartialConfig;
import hw5.model.Size;
import hw5.transform.VariationRegistry;
import org.junit.jupiter.api.Test;

class PerformanceBenchmarkTest {
    @Test
    void shouldMeasureSingleAndMultiThreadExecution() {
        FlameRenderer renderer = new FlameRenderer(new VariationRegistry());

        long t1 = run(renderer, 1);
        long t2 = run(renderer, 2);
        long t4 = run(renderer, 4);
        long t8 = run(renderer, 8);

        assertTrue(t1 > 0);
        assertTrue(t2 > 0);
        assertTrue(t4 > 0);
        assertTrue(t8 > 0);

        System.out.printf("Benchmark(ms): 1=%d, 2=%d, 4=%d, 8=%d%n", t1, t2, t4, t8);
    }

    private long run(FlameRenderer renderer, int threads) {
        GenerationConfig config = GenerationConfig.defaults().withOverrides(
            new PartialConfig(
                new Size(500, 350),
                300_000,
                "bench-test.png",
                threads,
                123L,
                null,
                null,
                1,
                20,
                2.2
            )
        );
        return renderer.render(config).elapsedMillis();
    }
}
