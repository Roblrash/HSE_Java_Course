package array;

import array.strategy.CapacityStrategy;
import array.strategy.DoublingStrategy;

import java.util.Arrays;

public class DynamicIntArray implements DynamicArray {

    private int[] data;
    private int size;
    private final CapacityStrategy strategy;

    public DynamicIntArray() {
        this(new DoublingStrategy());
    }

    public DynamicIntArray(CapacityStrategy strategy) {
        this.strategy = strategy;
        this.data = new int[10];
    }

    private void ensureCapacity(int required) {
        if (required <= data.length) return;
        int newCapacity = strategy.calculateNewCapacity(data.length, required);
        data = Arrays.copyOf(data, newCapacity);
    }

    @Override
    public boolean add(int e) {
        ensureCapacity(size + 1);
        data[size++] = e;
        return true;
    }

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public boolean contains(int element) {
        return indexOf(element) >= 0;
    }

    @Override
    public void clear() { size = 0; }

    @Override
    public int get(int index) {
        checkIndex(index);
        return data[index];
    }

    @Override
    public int set(int index, int element) {
        checkIndex(index);
        int old = data[index];
        data[index] = element;
        return old;
    }

    @Override
    public void add(int index, int element) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    @Override
    public long remove(int index) {
        checkIndex(index);
        int removed = data[index];
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        size--;
        return removed;
    }

    @Override
    public int indexOf(int element) {
        for (int i = 0; i < size; i++)
            if (data[i] == element) return i;
        return -1;
    }

    @Override
    public int lastIndexOf(int element) {
        for (int i = size - 1; i >= 0; i--)
            if (data[i] == element) return i;
        return -1;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();
    }
}
