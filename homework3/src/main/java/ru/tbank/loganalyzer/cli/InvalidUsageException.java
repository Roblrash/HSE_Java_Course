package ru.tbank.loganalyzer.cli;

public class InvalidUsageException extends RuntimeException {
    public InvalidUsageException(String message) {
        super(message);
    }
}
