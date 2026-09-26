package orm.annotation.validation;

import orm.core.validation.SizeValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = SizeValidator.class)
public @interface Size {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "size is out of range";
}
