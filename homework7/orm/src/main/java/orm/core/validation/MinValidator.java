package orm.core.validation;

import orm.annotation.validation.Min;
import orm.core.OrmException;

public class MinValidator implements ConstraintValidator<Min, Object> {
    @Override
    public boolean isValid(Object value, Min annotation) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number)) {
            throw new OrmException("@Min can be used only with numeric fields");
        }
        return ((Number) value).doubleValue() >= annotation.value();
    }
}
