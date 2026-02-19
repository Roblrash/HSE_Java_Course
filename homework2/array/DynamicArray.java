package array;

public interface DynamicArray {

    int size();
    boolean isEmpty();
    boolean contains(int element);
    boolean add(int e);
    void clear();
    int get(int index);
    int set(int index, int element);
    void add(int index, int element);
    long remove(int index);
    int indexOf(int element);
    int lastIndexOf(int element);
}
