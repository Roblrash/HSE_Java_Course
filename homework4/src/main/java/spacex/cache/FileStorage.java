package spacex.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileStorage {
    private static final String META_FILE_NAME = "cache_meta.json";

    private final File cacheDir;
    private final File metaFile;
    private final Gson gson;
    private Map<String, Long> meta;

    public FileStorage(String cacheDirPath) {
        this.cacheDir = new File(cacheDirPath);
        this.metaFile = new File(cacheDir, META_FILE_NAME);
        this.gson = new GsonBuilder().serializeNulls().create();
        this.meta = new HashMap<>();
        ensureDirectory();
        loadMeta();
    }

    public synchronized void write(String fileName, String content) throws IOException {
        ensureDirectory();
        File file = new File(cacheDir, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        meta.put(fileName, System.currentTimeMillis());
        saveMeta();
    }

    public synchronized Optional<String> read(String fileName) throws IOException {
        File file = new File(cacheDir, fileName);
        if (!file.exists()) {
            return Optional.empty();
        }
        return Optional.of(readText(file));
    }

    public synchronized boolean isCacheValid(String fileName, long ttlMillis) {
        File file = new File(cacheDir, fileName);
        if (!file.exists()) {
            return false;
        }
        Long savedAt = meta.get(fileName);
        if (savedAt == null) {
            return false;
        }
        return (System.currentTimeMillis() - savedAt) <= ttlMillis;
    }

    public synchronized Optional<Long> getSavedAt(String fileName) {
        return Optional.ofNullable(meta.get(fileName));
    }

    public synchronized void clear() throws IOException {
        ensureDirectory();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete() && file.exists()) {
                    throw new IOException("Не удалось удалить файл кеша: " + file.getName());
                }
            }
        }
        meta = new HashMap<>();
        saveMeta();
    }

    private void ensureDirectory() {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    private void loadMeta() {
        if (!metaFile.exists()) {
            meta = new HashMap<>();
            return;
        }

        try {
            String text = readText(metaFile);
            Type type = new TypeToken<Map<String, Long>>() {}.getType();
            Map<String, Long> loaded = gson.fromJson(text, type);
            meta = loaded == null ? new HashMap<>() : new HashMap<>(loaded);
        } catch (Exception ignored) {
            meta = new HashMap<>();
        }
    }

    private void saveMeta() throws IOException {
        ensureDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile))) {
            writer.write(gson.toJson(meta));
        }
    }

    private String readText(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    public synchronized Map<String, Long> getMetaSnapshot() {
        return Collections.unmodifiableMap(meta);
    }
}
