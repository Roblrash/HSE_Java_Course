package serialization;

import array.DynamicArray;
import array.DynamicIntArray;

public class CsvArraySerializer implements ArraySerializer {

    @Override
    public String serialize(DynamicArray array) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.size(); i++) {
            sb.append(array.get(i));
            if (i < array.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    @Override
    public DynamicArray deserialize(String data) {

        DynamicIntArray array = new DynamicIntArray();

        if (data == null || data.isBlank()) {
            return array;
        }

        String[] parts = data.split(",");

        for (String part : parts) {
            array.add(Integer.parseInt(part.trim()));
        }

        return array;
    }
}
