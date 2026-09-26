package orm.core.validation;

import orm.core.OrmException;

import java.util.Collections;
import java.util.List;

public class ValidationException extends OrmException {
    private final List<Violation> violations;

    public ValidationException(List<Violation> violations) {
        super("Validation failed: " + violations);
        this.violations = Collections.unmodifiableList(violations);
    }

    public List<Violation> getViolations() {
        return violations;
    }
}
