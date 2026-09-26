package orm.annotation.validation;

import orm.core.validation.PatternValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = PatternValidator.class)
public @interface Pattern {
    String regex();

    String message() default "must match pattern";
}
