package connection;

public interface Connection extends AutoCloseable {

    void execute(String command);

    @Override
    void close();
}
