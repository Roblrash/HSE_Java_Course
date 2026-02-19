package serialization;

import array.DynamicArray;
import array.DynamicIntArray;

import java.nio.ByteBuffer;
import java.util.Base64;

public class BinaryArraySerializer implements ArraySerializer {

    @Override
    public String serialize(DynamicArray array) {

        ByteBuffer buffer = ByteBuffer.allocate(array.size() * 4);

        for (int i = 0; i < array.size(); i++) {
            buffer.putInt(array.get(i));
        }

        return Base64.getEncoder().encodeToString(buffer.array());
    }

    @Override
    public DynamicArray deserialize(String data) {

        DynamicIntArray array = new DynamicIntArray();

        if (data == null || data.isBlank()) {
            return array;
        }

        byte[] bytes = Base64.getDecoder().decode(data);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        while (buffer.remaining() >= 4) {
            array.add(buffer.getInt());
        }

        return array;
    }
}
