package hw5.model;

import java.util.Map;

public record FunctionSpec(String name, double weight, Map<String, Double> params) {
    public FunctionSpec(String name, double weight) {
        this(name, weight, Map.of());
    }
}
