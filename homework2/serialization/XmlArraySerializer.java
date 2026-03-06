package serialization;

import array.DynamicArray;
import array.DynamicIntArray;

public class XmlArraySerializer implements ArraySerializer {

    @Override
    public String serialize(DynamicArray array) {

        StringBuilder sb = new StringBuilder();
        sb.append("<array>");

        for (int i = 0; i < array.size(); i++) {
            sb.append("<element>").append(array.get(i)).append("</element>");
        }

        sb.append("</array>");

        return sb.toString();
    }

    @Override
    public DynamicArray deserialize(String data) {

        DynamicIntArray array = new DynamicIntArray();

        if (data == null || data.isBlank()) {
            return array;
        }

        String[] parts = data.split("<element>");

        for (String part : parts) {
            if (part.contains("</element>")) {
                String value = part.substring(0, part.indexOf("</element>"));
                array.add(Integer.parseInt(value.trim()));
            }
        }

        return array;
    }
}
