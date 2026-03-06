package array.strategy;

public class DoublingStrategy implements CapacityStrategy {

    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int newCapacity = currentCapacity * 2;
        return Math.max(newCapacity, requiredCapacity);
    }
}
