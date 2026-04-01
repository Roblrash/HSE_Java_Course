package spacex.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonBuilder {
    private final Gson gson;

    public JsonBuilder() {
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public String buildDateRangeQuery(String startDate, String endDate) {
        Map<String, Object> root = new LinkedHashMap<>();
        Map<String, Object> query = new LinkedHashMap<>();
        Map<String, String> dateUtc = new LinkedHashMap<>();
        dateUtc.put("$gte", startDate + "T00:00:00.000Z");
        dateUtc.put("$lte", endDate + "T23:59:59.999Z");
        query.put("date_utc", dateUtc);

        Map<String, Object> options = new LinkedHashMap<>();
        Map<String, String> sort = new LinkedHashMap<>();
        sort.put("date_utc", "asc");
        options.put("sort", sort);

        root.put("query", query);
        root.put("options", options);

        return gson.toJson(root);
    }

    public String buildSuccessQuery(boolean success) {
        Map<String, Object> root = new LinkedHashMap<>();
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("success", success);

        Map<String, Object> options = new LinkedHashMap<>();
        Map<String, String> sort = new LinkedHashMap<>();
        sort.put("flight_number", "asc");
        options.put("sort", sort);

        root.put("query", query);
        root.put("options", options);
        return gson.toJson(root);
    }
}
