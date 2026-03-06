package array.strategy;

public class GoldenRatioStrategy implements CapacityStrategy {

    private static final double GOLDEN_RATIO = 1.618;

    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int newCapacity = (int) (currentCapacity * GOLDEN_RATIO);
        return Math.max(newCapacity, requiredCapacity);
    }
}
