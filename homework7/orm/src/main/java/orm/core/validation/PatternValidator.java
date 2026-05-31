package orm.core.validation;

import orm.annotation.validation.Pattern;
import orm.core.OrmException;

public class PatternValidator implements ConstraintValidator<Pattern, Object> {
    @Override
    public boolean isValid(Object value, Pattern annotation) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof String)) {
            throw new OrmException("@Pattern can be used only with String fields");
        }
        return java.util.regex.Pattern.matches(annotation.regex(), (String) value);
    }
}
