package connection;

import java.util.Random;

public class DefaultConnectionManager implements ConnectionManager {

    private static final Random RANDOM = new Random();

    @Override
    public Connection getConnection() {
        if (RANDOM.nextBoolean()) {
            return new FaultyConnection();
        }
        return new StableConnection();
    }
}
