package hw5.render;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import hw5.config.GenerationConfig;
import hw5.model.AffineTransform;
import hw5.model.FunctionSpec;
import hw5.model.Point2D;
import hw5.model.RgbColor;
import hw5.transform.Variation;
import hw5.transform.VariationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FlameRenderer {
    private static final Logger log = LoggerFactory.getLogger(FlameRenderer.class);

    private static final double WORLD_MIN_X = -1.5;
    private static final double WORLD_MAX_X = 1.5;
    private static final double WORLD_MIN_Y = -1.0;
    private static final double WORLD_MAX_Y = 1.0;

    private final VariationRegistry variationRegistry;

    public FlameRenderer(VariationRegistry variationRegistry) {
        this.variationRegistry = variationRegistry;
    }

    public RenderedImage render(GenerationConfig config) {
        long start = System.nanoTime();

        List<Variation> variations = resolveVariations(config.functions());
        List<RgbColor> affineColors = generateAffineColors(config.affineParams().size(), config.seed());

        int threads = config.threads();
        long totalIterations = (long) config.iterationCount();
        long perThread = totalIterations / threads;
        long remainder = totalIterations % threads;

        log.info("Rendering started: {}x{}, iterations={}, threads={}",
            config.size().width(), config.size().height(), config.iterationCount(), threads);

        AtomicLong progress = new AtomicLong(0L);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<PixelBuffer>> futures = new ArrayList<>();

        try {
            for (int t = 0; t < threads; t++) {
                long iterations = perThread + (t < remainder ? 1 : 0);
                long seed = config.seed() + t * 1_000_003L;
                futures.add(pool.submit(new RenderTask(
                    iterations,
                    seed,
                    config,
                    config.affineParams(),
                    config.functions(),
                    variations,
                    affineColors,
                    progress
                )));
            }

            long nextLog = Math.max(1, totalIterations / 20);
            while (progress.get() < totalIterations) {
                boolean allDone = true;
                for (Future<PixelBuffer> future : futures) {
                    if (!future.isDone()) {
                        allDone = false;
                        break;
                    }
                }
                if (allDone) {
                    break;
                }

                long done = progress.get();
                if (done >= nextLog) {
                    log.info("Progress: {}% ({}/{})", (100 * done / totalIterations), done, totalIterations);
                    nextLog += Math.max(1, totalIterations / 20);
                }
                Thread.sleep(120);
            }

            PixelBuffer merged = new PixelBuffer(config.size().width(), config.size().height());
            for (Future<PixelBuffer> future : futures) {
                merged.merge(future.get());
            }

            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.info("Rendering completed in {} ms", elapsedMs);
            return new RenderedImage(ToneMapper.toImage(merged, config.gamma()), elapsedMs);

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rendering interrupted", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("Rendering failed", ex.getCause());
        } finally {
            pool.shutdownNow();
        }
    }

    private List<Variation> resolveVariations(List<FunctionSpec> specs) {
        List<Variation> vars = new ArrayList<>();
        for (FunctionSpec spec : specs) {
            vars.add(variationRegistry.getOrThrow(spec.name()));
        }
        return vars;
    }

    private List<RgbColor> generateAffineColors(int count, long seed) {
        SplittableRandom random = new SplittableRandom(seed ^ 0xA5A5A5A5L);
        List<RgbColor> colors = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            colors.add(RgbColor.fromIndex(random.nextDouble()));
        }
        return colors;
    }

    private static final class RenderTask implements Callable<PixelBuffer> {
        private final long iterations;
        private final long seed;
        private final GenerationConfig config;
        private final List<AffineTransform> affines;
        private final List<FunctionSpec> functionSpecs;
        private final List<Variation> variations;
        private final List<RgbColor> affineColors;
        private final AtomicLong progress;

        private RenderTask(
            long iterations,
            long seed,
            GenerationConfig config,
            List<AffineTransform> affines,
            List<FunctionSpec> functionSpecs,
            List<Variation> variations,
            List<RgbColor> affineColors,
            AtomicLong progress
        ) {
            this.iterations = iterations;
            this.seed = seed;
            this.config = config;
            this.affines = affines;
            this.functionSpecs = functionSpecs;
            this.variations = variations;
            this.affineColors = affineColors;
            this.progress = progress;
        }

        @Override
        public PixelBuffer call() {
            PixelBuffer buffer = new PixelBuffer(config.size().width(), config.size().height());
            WeightedIndexPicker functionPicker = new WeightedIndexPicker(functionSpecs.stream().mapToDouble(FunctionSpec::weight).toArray());
            SplittableRandom random = new SplittableRandom(seed);

            Point2D point = new Point2D(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));
            RgbColor currentColor = RgbColor.fromIndex(random.nextDouble());

            long reportStep = Math.max(1L, iterations / 200L);
            long localProgress = 0L;
            for (long i = 0; i < iterations; i++) {
                int affineIndex = random.nextInt(affines.size());
                AffineTransform affine = affines.get(affineIndex);
                point = affine.apply(point);

                int variationIndex = functionPicker.pick(random.nextDouble());
                Variation variation = variations.get(variationIndex);
                point = variation.apply(point, functionSpecs.get(variationIndex).params());

                RgbColor affineColor = affineColors.get(affineIndex);
                currentColor = new RgbColor(
                    (currentColor.r() + affineColor.r()) * 0.5,
                    (currentColor.g() + affineColor.g()) * 0.5,
                    (currentColor.b() + affineColor.b()) * 0.5
                );

                if (i >= config.warmupIterations() && point.isFinite()) {
                    plotWithSymmetry(buffer, point, currentColor, config.symmetryLevel());
                }

                localProgress++;
                if (localProgress >= reportStep || i == iterations - 1) {
                    progress.addAndGet(localProgress);
                    localProgress = 0L;
                }
            }
            return buffer;
        }

        private static void plotWithSymmetry(PixelBuffer buffer, Point2D point, RgbColor color, int symmetryLevel) {
            for (int s = 0; s < symmetryLevel; s++) {
                double angle = (2.0 * Math.PI * s) / symmetryLevel;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double rx = point.x() * cos - point.y() * sin;
                double ry = point.x() * sin + point.y() * cos;

                int px = (int) ((rx - WORLD_MIN_X) / (WORLD_MAX_X - WORLD_MIN_X) * (buffer.width() - 1));
                int py = (int) ((WORLD_MAX_Y - ry) / (WORLD_MAX_Y - WORLD_MIN_Y) * (buffer.height() - 1));
                buffer.add(px, py, color);
            }
        }
    }
}
