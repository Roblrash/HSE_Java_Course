package spacex.service;

public class ApiResult<T> {
    private final T data;
    private final boolean fromCache;
    private final Long cachedAt;

    public ApiResult(T data, boolean fromCache, Long cachedAt) {
        this.data = data;
        this.fromCache = fromCache;
        this.cachedAt = cachedAt;
    }

    public T getData() {
        return data;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public Long getCachedAt() {
        return cachedAt;
    }
}
