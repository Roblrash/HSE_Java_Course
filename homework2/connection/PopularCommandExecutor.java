package connection;

public final class PopularCommandExecutor {

    private final ConnectionManager manager;
    private final int maxAttempts;

    public PopularCommandExecutor(ConnectionManager manager, int maxAttempts) {
        this.manager = manager;
        this.maxAttempts = maxAttempts;
    }

    public void updatePackages() {
        tryExecute("apt update && apt upgrade -y");
    }

    void tryExecute(String command) {

        int attempts = 0;
        ConnectionException lastException = null;

        while (attempts < maxAttempts) {
            try (Connection connection = manager.getConnection()) {
                connection.execute(command);
                return;
            } catch (ConnectionException e) {
                lastException = e;
                attempts++;
            }
        }

        throw new ConnectionException("Failed after " + maxAttempts + " attempts", lastException);
    }
}
