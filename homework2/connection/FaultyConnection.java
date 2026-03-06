package connection;

import java.util.Random;

public class FaultyConnection implements Connection {

    private static final Random RANDOM = new Random();

    @Override
    public void execute(String command) {
        if (RANDOM.nextBoolean()) {
            throw new ConnectionException("Connection failed");
        }
        System.out.println("Executing command: " + command);
    }

    @Override
    public void close() {
        System.out.println("Connection closed");
    }
}
