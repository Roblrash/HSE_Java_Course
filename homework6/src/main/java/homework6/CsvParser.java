package homework6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CsvParser {

    public <T> List<T> parseFromCsv(String filename, Class<T> klass) {
        Objects.requireNonNull(filename, "filename must not be null");
        Objects.requireNonNull(klass, "klass must not be null");

        try (InputStream raw = openInputStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(raw, StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                return Collections.emptyList();
            }

            List<String> headers = splitCsvLine(headerLine);
            List<FieldMapping> mappings = resolveMappings(klass, headers);

            List<T> result = new ArrayList<T>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> cells = splitCsvLine(line);
                T instance = createInstance(klass);

                for (int i = 0; i < mappings.size() && i < cells.size(); i++) {
                    FieldMapping mapping = mappings.get(i);
                    if (mapping == null) {
                        continue;
                    }
                    setValueByPath(instance, mapping.path, cells.get(i), mapping.collectionDelimiter);
                }
                result.add(instance);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + filename, e);
        }
    }

    public <T> void saveToCsv(String filename, Collection<T> collection, Class<T> klass) {
        Objects.requireNonNull(filename, "filename must not be null");
        Objects.requireNonNull(collection, "collection must not be null");
        Objects.requireNonNull(klass, "klass must not be null");

        List<LeafField> leaves = new ArrayList<LeafField>();
        collectLeafFields(klass, new ArrayList<Field>(), "", leaves);

        try (OutputStream raw = openOutputStream(filename);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(raw, StandardCharsets.UTF_8))) {

            List<String> headers = new ArrayList<String>();
            for (LeafField leaf : leaves) {
                headers.add(leaf.header);
            }
            writer.write(String.join(",", headers));
            writer.newLine();

            for (T item : collection) {
                List<String> row = new ArrayList<String>();
                for (LeafField leaf : leaves) {
                    Object value = getValueByPath(item, leaf.path);
                    row.add(formatValue(value, leaf));
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save CSV file: " + filename, e);
        }
    }

    private InputStream openInputStream(String filename) throws IOException {
        if (filename.endsWith(".csv")) {
            return new FileInputStream(filename);
        }
        if (filename.endsWith(".csv.gz") || filename.endsWith(".gz")) {
            return new GZIPInputStream(new FileInputStream(filename));
        }
        if (filename.endsWith(".zip")) {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                zis.close();
                throw new IllegalArgumentException("ZIP archive is empty: " + filename);
            }
            return zis;
        }
        throw new IllegalArgumentException("Unsupported file extension for input: " + filename);
    }

    private OutputStream openOutputStream(String filename) throws IOException {
        if (filename.endsWith(".csv")) {
            return new FileOutputStream(filename);
        }
        if (filename.endsWith(".csv.gz") || filename.endsWith(".gz")) {
            return new GZIPOutputStream(new FileOutputStream(filename));
        }
        if (filename.endsWith(".zip")) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));
            String entryName = buildZipEntryName(filename);
            zos.putNextEntry(new ZipEntry(entryName));
            return zos;
        }
        throw new IllegalArgumentException("Unsupported file extension for output: " + filename);
    }

    private String buildZipEntryName(String filename) {
        String name = filename;
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        if (name.endsWith(".zip")) {
            name = name.substring(0, name.length() - 4);
        }
        if (!name.endsWith(".csv")) {
            name = name + ".csv";
        }
        return name;
    }

    private List<String> splitCsvLine(String line) {
        String[] parts = line.split(",", -1);
        List<String> result = new ArrayList<String>(parts.length);
        Collections.addAll(result, parts);
        return result;
    }

    private <T> List<FieldMapping> resolveMappings(Class<T> rootClass, List<String> headers) {
        List<LeafField> leaves = new ArrayList<LeafField>();
        collectLeafFields(rootClass, new ArrayList<Field>(), "", leaves);

        Map<String, LeafField> byHeader = new HashMap<String, LeafField>();
        for (LeafField leaf : leaves) {
            byHeader.put(leaf.header, leaf);
        }

        List<FieldMapping> mappings = new ArrayList<FieldMapping>();
        for (String header : headers) {
            LeafField leaf = byHeader.get(header);
            if (leaf == null) {
                mappings.add(null);
            } else {
                mappings.add(new FieldMapping(leaf.path, leaf.collectionDelimiter));
            }
        }
        return mappings;
    }

    private void collectLeafFields(Class<?> klass, List<Field> currentPath, String prefix, List<LeafField> out) {
        for (Field field : klass.getDeclaredFields()) {
            String ownName = resolveCsvName(field);
            String headerName = prefix.isEmpty() ? ownName : prefix + "." + ownName;

            if (isListField(field)) {
                Class<?> elementType = extractListElementType(field);
                if (!isSimpleSupportedType(elementType)) {
                    throw new IllegalArgumentException("Unsupported list element type for field: " + field);
                }
                List<Field> newPath = new ArrayList<Field>(currentPath);
                newPath.add(field);
                out.add(new LeafField(newPath, headerName, field, true));
                continue;
            }

            if (isSimpleSupportedType(field.getType())) {
                List<Field> newPath = new ArrayList<Field>(currentPath);
                newPath.add(field);
                out.add(new LeafField(newPath, headerName, field, false));
            } else {
                List<Field> newPath = new ArrayList<Field>(currentPath);
                newPath.add(field);
                collectLeafFields(field.getType(), newPath, headerName, out);
            }
        }
    }

    private String resolveCsvName(Field field) {
        CsvName csvName = field.getAnnotation(CsvName.class);
        return csvName == null ? field.getName() : csvName.value();
    }

    private boolean isSimpleSupportedType(Class<?> type) {
        return type == int.class || type == Integer.class
                || type == long.class || type == Long.class
                || type == double.class || type == Double.class
                || type == boolean.class || type == Boolean.class
                || type == String.class;
    }

    private boolean isListField(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    private Class<?> extractListElementType(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("List field must declare generic type: " + field);
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        if (typeArgs.length != 1 || !(typeArgs[0] instanceof Class)) {
            throw new IllegalArgumentException("Unsupported list generic type for field: " + field);
        }
        return (Class<?>) typeArgs[0];
    }

    private <T> T createInstance(Class<T> klass) {
        try {
            Constructor<T> constructor = klass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate class: " + klass.getName(), e);
        }
    }

    private void setValueByPath(Object root, List<Field> path, String rawValue, String collectionDelimiter) {
        try {
            Object current = root;
            for (int i = 0; i < path.size() - 1; i++) {
                Field field = path.get(i);
                field.setAccessible(true);
                Object nested = field.get(current);
                if (nested == null) {
                    nested = createInstance(field.getType());
                    field.set(current, nested);
                }
                current = nested;
            }

            Field leaf = path.get(path.size() - 1);
            leaf.setAccessible(true);

            if (isListField(leaf)) {
                Class<?> elementType = extractListElementType(leaf);
                List<Object> parsed = parseListValue(rawValue, collectionDelimiter, elementType);
                leaf.set(current, parsed);
            } else {
                Object value = parseSimpleValue(rawValue, leaf.getType());
                leaf.set(current, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set value by path", e);
        }
    }

    private List<Object> parseListValue(String rawValue, String delimiter, Class<?> elementType) {
        if (rawValue == null || rawValue.isEmpty()) {
            return new ArrayList<Object>();
        }

        String[] parts = rawValue.split(java.util.regex.Pattern.quote(delimiter), -1);
        List<Object> values = new ArrayList<Object>(parts.length);
        for (String part : parts) {
            values.add(parseSimpleValue(part, elementType));
        }
        return values;
    }

    private Object parseSimpleValue(String rawValue, Class<?> type) {
        if (type == String.class) {
            return rawValue;
        }
        if (type == int.class || type == Integer.class) {
            return rawValue == null || rawValue.isEmpty() ? 0 : Integer.parseInt(rawValue);
        }
        if (type == long.class || type == Long.class) {
            return rawValue == null || rawValue.isEmpty() ? 0L : Long.parseLong(rawValue);
        }
        if (type == double.class || type == Double.class) {
            return rawValue == null || rawValue.isEmpty() ? 0.0d : Double.parseDouble(rawValue);
        }
        if (type == boolean.class || type == Boolean.class) {
            return rawValue != null && Boolean.parseBoolean(rawValue);
        }
        throw new IllegalArgumentException("Unsupported field type: " + type.getName());
    }

    private Object getValueByPath(Object root, List<Field> path) {
        try {
            Object current = root;
            for (Field field : path) {
                if (current == null) {
                    return null;
                }
                field.setAccessible(true);
                current = field.get(current);
            }
            return current;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get value by path", e);
        }
    }

    private String formatValue(Object value, LeafField leaf) {
        if (value == null) {
            return "";
        }
        if (leaf.listField) {
            List<?> list = (List<?>) value;
            List<String> parts = new ArrayList<String>();
            for (Object item : list) {
                parts.add(item == null ? "" : String.valueOf(item));
            }
            return String.join(leaf.collectionDelimiter, parts);
        }
        return String.valueOf(value);
    }

    private static class LeafField {
        private final List<Field> path;
        private final String header;
        private final boolean listField;
        private final String collectionDelimiter;

        private LeafField(List<Field> path, String header, Field field, boolean listField) {
            this.path = path;
            this.header = header;
            this.listField = listField;
            CsvCollection ann = field.getAnnotation(CsvCollection.class);
            this.collectionDelimiter = listField ? (ann == null ? "|" : ann.delimiter()) : "";
        }
    }

    private static class FieldMapping {
        private final List<Field> path;
        private final String collectionDelimiter;

        private FieldMapping(List<Field> path, String collectionDelimiter) {
            this.path = path;
            this.collectionDelimiter = collectionDelimiter;
        }
    }
}
