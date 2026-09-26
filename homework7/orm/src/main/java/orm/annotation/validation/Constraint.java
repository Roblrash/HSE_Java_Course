package orm.annotation.validation;

import orm.core.validation.ConstraintValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Constraint {
    Class<? extends ConstraintValidator<?, ?>> validatedBy();
}
