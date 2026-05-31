package orm.annotation.validation;

import orm.core.validation.NotNullValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = NotNullValidator.class)
public @interface NotNull {
    String message() default "must not be null";
}
