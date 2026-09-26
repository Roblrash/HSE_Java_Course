package orm.core.validation;

import orm.annotation.validation.Size;
import orm.core.OrmException;

public class SizeValidator implements ConstraintValidator<Size, Object> {
    @Override
    public boolean isValid(Object value, Size annotation) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof String)) {
            throw new OrmException("@Size can be used only with String fields");
        }
        int length = ((String) value).length();
        return length >= annotation.min() && length <= annotation.max();
    }
}
