package spacex.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {
    private final Path tempDir;
    private final FileStorage storage;

    FileStorageTest() throws IOException {
        this.tempDir = Files.createTempDirectory("spacex-cache-test");
        this.storage = new FileStorage(tempDir.toString());
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                    }
                });
    }

    @Test
    void writeAndReadRoundTrip() throws IOException {
        storage.write("launches_all.json", "[1,2,3]");

        String content = storage.read("launches_all.json").orElseThrow();

        assertEquals("[1,2,3]", content);
    }

    @Test
    void readReturnsEmptyForMissingFile() throws IOException {
        assertTrue(storage.read("missing.json").isEmpty());
    }

    @Test
    void writeOverwritesExistingFile() throws IOException {
        storage.write("launches_all.json", "old");
        storage.write("launches_all.json", "new");

        String content = storage.read("launches_all.json").orElseThrow();

        assertEquals("new", content);
    }

    @Test
    void isCacheValidReturnsTrueWhenWithinTtl() throws IOException {
        storage.write("launches_all.json", "data");

        assertTrue(storage.isCacheValid("launches_all.json", 10_000));
    }

    @Test
    void isCacheValidReturnsFalseWhenTtlExpired() throws Exception {
        storage.write("launches_all.json", "data");
        Thread.sleep(20);

        assertFalse(storage.isCacheValid("launches_all.json", 1));
    }

    @Test
    void clearRemovesAllCacheData() throws IOException {
        storage.write("launches_all.json", "data");
        storage.write("launches_latest.json", "data2");

        storage.clear();

        assertTrue(storage.read("launches_all.json").isEmpty());
        assertTrue(storage.read("launches_latest.json").isEmpty());
        assertTrue(storage.getMetaSnapshot().isEmpty());
    }
}
