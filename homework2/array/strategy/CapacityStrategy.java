package array.strategy;

public interface CapacityStrategy {
    int calculateNewCapacity(int currentCapacity, int requiredCapacity);
}
