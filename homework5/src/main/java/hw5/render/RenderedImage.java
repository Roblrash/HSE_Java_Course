package hw5.render;

import java.awt.image.BufferedImage;

public record RenderedImage(BufferedImage image, long elapsedMillis) {
}
