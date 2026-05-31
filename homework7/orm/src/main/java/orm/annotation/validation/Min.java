package orm.annotation.validation;

import orm.core.validation.MinValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = MinValidator.class)
public @interface Min {
    long value();

    String message() default "must be greater than or equal to minimum";
}
