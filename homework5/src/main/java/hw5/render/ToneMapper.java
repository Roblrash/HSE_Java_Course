package hw5.render;

import java.awt.image.BufferedImage;

public final class ToneMapper {
    private ToneMapper() {
    }

    public static BufferedImage toImage(PixelBuffer buffer, double gamma) {
        BufferedImage image = new BufferedImage(buffer.width(), buffer.height(), BufferedImage.TYPE_INT_RGB);

        int[] hits = buffer.hits();
        double[] r = buffer.r();
        double[] g = buffer.g();
        double[] b = buffer.b();

        int maxHit = 0;
        for (int hit : hits) {
            if (hit > maxHit) {
                maxHit = hit;
            }
        }

        double maxLog = Math.log(maxHit + 1.0);
        if (maxHit == 0 || maxLog == 0.0) {
            return image;
        }

        for (int y = 0; y < buffer.height(); y++) {
            for (int x = 0; x < buffer.width(); x++) {
                int idx = y * buffer.width() + x;
                int hit = hits[idx];
                if (hit <= 0) {
                    continue;
                }

                double scale = Math.log(hit + 1.0) / hit;
                double rr = r[idx] * scale;
                double gg = g[idx] * scale;
                double bb = b[idx] * scale;

                double density = Math.log(hit + 1.0) / maxLog;
                double gammaCorrect = Math.pow(density, 1.0 / gamma);

                rr *= gammaCorrect;
                gg *= gammaCorrect;
                bb *= gammaCorrect;

                int rgb = ((int) Math.round(hw5.util.MathUtils.clamp(rr, 0.0, 1.0) * 255.0) << 16)
                    | ((int) Math.round(hw5.util.MathUtils.clamp(gg, 0.0, 1.0) * 255.0) << 8)
                    | (int) Math.round(hw5.util.MathUtils.clamp(bb, 0.0, 1.0) * 255.0);
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }
}

