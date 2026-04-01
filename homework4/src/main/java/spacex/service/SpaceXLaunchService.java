package spacex.service;

import spacex.cache.FileStorage;
import spacex.http.SpaceXHttpClient;
import spacex.json.JsonBuilder;
import spacex.json.JsonParser;
import spacex.model.Launch;

import java.io.IOException;
import java.util.List;

public class SpaceXLaunchService {
    private static final String ALL_CACHE_FILE = "launches_all.json";
    private static final String LATEST_CACHE_FILE = "launches_latest.json";

    private final SpaceXHttpClient httpClient;
    private final JsonParser jsonParser;
    private final JsonBuilder jsonBuilder;
    private final FileStorage fileStorage;
    private final String baseUrl;
    private final long ttlMillis;

    public SpaceXLaunchService(SpaceXHttpClient httpClient,
                               JsonParser jsonParser,
                               JsonBuilder jsonBuilder,
                               FileStorage fileStorage,
                               String baseUrl,
                               long ttlMillis) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.jsonBuilder = jsonBuilder;
        this.fileStorage = fileStorage;
        this.baseUrl = baseUrl;
        this.ttlMillis = ttlMillis;
    }

    public ApiResult<List<Launch>> getAllLaunches() throws IOException {
        CachedBody cachedBody = fetchWithCache(ALL_CACHE_FILE,
                () -> httpClient.get(SpaceXHttpClient.buildUrl(baseUrl, "/v5/launches")));
        return new ApiResult<>(jsonParser.parseLaunches(cachedBody.body), cachedBody.fromCache, cachedBody.cachedAt);
    }

    public ApiResult<Launch> getLatestLaunch() throws IOException {
        CachedBody cachedBody = fetchWithCache(LATEST_CACHE_FILE,
                () -> httpClient.get(SpaceXHttpClient.buildUrl(baseUrl, "/v5/launches/latest")));
        return new ApiResult<>(jsonParser.parseLaunch(cachedBody.body), cachedBody.fromCache, cachedBody.cachedAt);
    }

    public ApiResult<List<Launch>> searchByDate(String startDate, String endDate) throws IOException {
        String cacheKey = "query_" + startDate + "_" + endDate + ".json";
        String body = jsonBuilder.buildDateRangeQuery(startDate, endDate);

        CachedBody cachedBody = fetchWithCache(cacheKey,
                () -> httpClient.post(SpaceXHttpClient.buildUrl(baseUrl, "/v5/launches/query"), body));

        return new ApiResult<>(jsonParser.parseQueryLaunches(cachedBody.body), cachedBody.fromCache, cachedBody.cachedAt);
    }

    public ApiResult<List<Launch>> searchBySuccess(boolean success) throws IOException {
        String cacheKey = "query_success_" + success + ".json";
        String body = jsonBuilder.buildSuccessQuery(success);

        CachedBody cachedBody = fetchWithCache(cacheKey,
                () -> httpClient.post(SpaceXHttpClient.buildUrl(baseUrl, "/v5/launches/query"), body));

        return new ApiResult<>(jsonParser.parseQueryLaunches(cachedBody.body), cachedBody.fromCache, cachedBody.cachedAt);
    }

    public void clearCache() throws IOException {
        fileStorage.clear();
    }

    private CachedBody fetchWithCache(String cacheFile, NetworkCall networkCall) throws IOException {
        try {
            String body = networkCall.call();
            fileStorage.write(cacheFile, body);
            return new CachedBody(body, false, null);
        } catch (IOException ex) {
            if (fileStorage.isCacheValid(cacheFile, ttlMillis)) {
                String cached = fileStorage.read(cacheFile).orElseThrow(() -> ex);
                Long cachedAt = fileStorage.getSavedAt(cacheFile).orElse(null);
                return new CachedBody(cached, true, cachedAt);
            }
            throw ex;
        }
    }

    @FunctionalInterface
    private interface NetworkCall {
        String call() throws IOException;
    }

    private static class CachedBody {
        private final String body;
        private final boolean fromCache;
        private final Long cachedAt;

        private CachedBody(String body, boolean fromCache, Long cachedAt) {
            this.body = body;
            this.fromCache = fromCache;
            this.cachedAt = cachedAt;
        }
    }
}
