package hw5.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WeightedIndexPickerTest {
    @Test
    void shouldPickValidIndex() {
        WeightedIndexPicker picker = new WeightedIndexPicker(new double[]{1.0, 2.0, 3.0});

        int idx1 = picker.pick(0.01);
        int idx2 = picker.pick(0.50);
        int idx3 = picker.pick(0.99);

        assertTrue(idx1 >= 0 && idx1 <= 2);
        assertTrue(idx2 >= 0 && idx2 <= 2);
        assertTrue(idx3 >= 0 && idx3 <= 2);
    }

    @Test
    void shouldBeDeterministicOnBoundary() {
        WeightedIndexPicker picker = new WeightedIndexPicker(new double[]{1.0, 1.0});
        assertEquals(0, picker.pick(0.2));
        assertEquals(1, picker.pick(0.8));
    }
}
