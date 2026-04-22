package hw5.benchmark;

import hw5.config.GenerationConfig;
import hw5.render.FlameRenderer;
import hw5.transform.VariationRegistry;

public final class BenchmarkRunner {
    private BenchmarkRunner() {
    }

    public static void main(String[] args) {
        int[] threadCounts = {1, 2, 4, 8};

        System.out.println("threads,time_ms");
        for (int threads : threadCounts) {
            GenerationConfig config = GenerationConfig.defaults().withOverrides(
                new hw5.config.PartialConfig(
                    new hw5.model.Size(1200, 800),
                    3_000_000,
                    "benchmark-" + threads + ".png",
                    threads,
                    42L,
                    null,
                    null,
                    3,
                    20,
                    2.2
                )
            );

            FlameRenderer renderer = new FlameRenderer(new VariationRegistry());
            long elapsed = renderer.render(config).elapsedMillis();
            System.out.println(threads + "," + elapsed);
        }
    }
}
