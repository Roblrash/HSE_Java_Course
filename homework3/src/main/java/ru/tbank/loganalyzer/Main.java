package ru.tbank.loganalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import ru.tbank.loganalyzer.cli.AnalyzerCommand;
import ru.tbank.loganalyzer.cli.InvalidUsageException;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new AnalyzerCommand());
        commandLine.setParameterExceptionHandler((exception, args1) -> {
            System.err.println(exception.getMessage());
            return 2;
        });
        commandLine.setExecutionExceptionHandler((exception, commandLine1, parseResult) -> {
            if (exception instanceof InvalidUsageException) {
                System.err.println(exception.getMessage());
                return 2;
            }
            LOGGER.error("Unexpected error during execution", exception);
            return 1;
        });

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
