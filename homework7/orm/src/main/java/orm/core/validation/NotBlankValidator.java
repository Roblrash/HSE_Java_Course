package orm.core.validation;

import orm.annotation.validation.NotBlank;
import orm.core.OrmException;

public class NotBlankValidator implements ConstraintValidator<NotBlank, Object> {
    @Override
    public boolean isValid(Object value, NotBlank annotation) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof String)) {
            throw new OrmException("@NotBlank can be used only with String fields");
        }
        return !((String) value).trim().isEmpty();
    }
}
