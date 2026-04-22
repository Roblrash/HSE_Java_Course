package spacex.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SpaceXHttpClient {
    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public SpaceXHttpClient() {
        this(10_000, 10_000);
    }

    public SpaceXHttpClient(int connectTimeoutMs, int readTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    public String get(String url) throws IOException {
        return execute("GET", url, null);
    }

    public String post(String url, String jsonBody) throws IOException {
        return execute("POST", url, jsonBody);
    }

    public static String buildUrl(String baseUrl, String path) {
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return normalizedBase + normalizedPath;
    }

    public String handleResponse(int statusCode, String responseBody) throws HttpRequestException {
        if (statusCode != HttpURLConnection.HTTP_OK) {
            throw new HttpRequestException(statusCode, responseBody);
        }
        return responseBody;
    }

    private String execute(String method, String url, String body) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);

            if ("POST".equals(method)) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                if (body != null) {
                    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                    try (OutputStream outputStream = connection.getOutputStream()) {
                        outputStream.write(bytes);
                    }
                }
            }

            int statusCode = connection.getResponseCode();
            String responseBody = readResponseBody(statusCode, connection);
            return handleResponse(statusCode, responseBody);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponseBody(int statusCode, HttpURLConnection connection) throws IOException {
        InputStream inputStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (inputStream == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
