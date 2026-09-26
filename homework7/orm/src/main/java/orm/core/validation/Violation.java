package orm.core.validation;

import java.lang.annotation.Annotation;

public final class Violation {
    private final String field;
    private final Object invalidValue;
    private final String message;
    private final Class<? extends Annotation> annotation;

    public Violation(String field, Object invalidValue, String message, Class<? extends Annotation> annotation) {
        this.field = field;
        this.invalidValue = invalidValue;
        this.message = message;
        this.annotation = annotation;
    }

    public String getField() {
        return field;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public String getMessage() {
        return message;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "field='" + field + '\'' +
                ", invalidValue=" + invalidValue +
                ", message='" + message + '\'' +
                ", annotation=" + annotation.getSimpleName() +
                '}';
    }
}
