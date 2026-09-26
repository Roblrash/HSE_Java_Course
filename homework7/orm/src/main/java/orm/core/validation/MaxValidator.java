package orm.core.validation;

import orm.annotation.validation.Max;
import orm.core.OrmException;

public class MaxValidator implements ConstraintValidator<Max, Object> {
    @Override
    public boolean isValid(Object value, Max annotation) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number)) {
            throw new OrmException("@Max can be used only with numeric fields");
        }
        return ((Number) value).doubleValue() <= annotation.value();
    }
}
