package hw5.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class PngWriter {
    public void write(BufferedImage image, String outputPath) throws IOException {
        Path path = Path.of(outputPath);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        boolean ok = ImageIO.write(image, "png", path.toFile());
        if (!ok) {
            throw new IOException("Failed to write PNG to: " + outputPath);
        }
    }
}
