import java.util.Random;

public enum Difficulty {
    EASY(8),
    MEDIUM(6),
    HARD(4);

    private final int maxAttempts;

    Difficulty(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public static Difficulty random() {
        Difficulty[] values = values();
        return values[new Random().nextInt(values.length)];
    }
}
