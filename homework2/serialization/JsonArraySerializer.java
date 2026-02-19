package serialization;

import array.DynamicArray;

public class JsonArraySerializer implements ArraySerializer {

    private final CsvArraySerializer csv = new CsvArraySerializer();

    @Override
    public String serialize(DynamicArray array) {
        return "[" + csv.serialize(array) + "]";
    }

    @Override
    public DynamicArray deserialize(String data) {

        if (data == null || data.isBlank()) {
            return csv.deserialize("");
        }

        String cleaned = data.replace("[", "").replace("]", "");
        return csv.deserialize(cleaned);
    }
}
