package serialization;

import array.DynamicArray;

public interface ArraySerializer {
    String serialize(DynamicArray array);
    DynamicArray deserialize(String data);
}
