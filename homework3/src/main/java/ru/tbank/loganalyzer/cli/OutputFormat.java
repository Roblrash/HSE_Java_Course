package ru.tbank.loganalyzer.cli;

public enum OutputFormat {
    JSON("json", ".json"),
    MARKDOWN("markdown", ".md");

    private final String cliValue;
    private final String extension;

    OutputFormat(String cliValue, String extension) {
        this.cliValue = cliValue;
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

    public static OutputFormat fromCliValue(String value) {
        for (OutputFormat format : values()) {
            if (format.cliValue.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new InvalidUsageException("Unsupported --format value: " + value + ". Supported: json, markdown");
    }
}
