package orm.annotation.validation;

import orm.core.validation.MaxValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = MaxValidator.class)
public @interface Max {
    long value();

    String message() default "must be less than or equal to maximum";
}
