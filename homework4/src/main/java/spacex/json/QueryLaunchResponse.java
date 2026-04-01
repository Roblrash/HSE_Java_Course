package spacex.json;

import spacex.model.Launch;

import java.util.List;

public class QueryLaunchResponse {
    private List<Launch> docs;

    public List<Launch> getDocs() {
        return docs;
    }
}
