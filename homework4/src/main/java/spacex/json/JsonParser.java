package spacex.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import spacex.model.Launch;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class JsonParser {
    private final Gson gson;

    public JsonParser() {
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public List<Launch> parseLaunches(String json) {
        try {
            Type listType = new TypeToken<List<Launch>>() {}.getType();
            List<Launch> launches = gson.fromJson(json, listType);
            return launches == null ? Collections.emptyList() : launches;
        } catch (JsonSyntaxException ex) {
            throw new IllegalArgumentException("Некорректный JSON для массива запусков", ex);
        }
    }

    public Launch parseLaunch(String json) {
        try {
            Launch launch = gson.fromJson(json, Launch.class);
            if (launch == null) {
                throw new IllegalArgumentException("Пустой JSON объекта запуска");
            }
            return launch;
        } catch (JsonSyntaxException ex) {
            throw new IllegalArgumentException("Некорректный JSON для запуска", ex);
        }
    }

    public List<Launch> parseQueryLaunches(String json) {
        try {
            QueryLaunchResponse response = gson.fromJson(json, QueryLaunchResponse.class);
            if (response == null || response.getDocs() == null) {
                return Collections.emptyList();
            }
            return response.getDocs();
        } catch (JsonSyntaxException ex) {
            throw new IllegalArgumentException("Некорректный JSON для query-ответа", ex);
        }
    }

    public Gson getGson() {
        return gson;
    }
}
