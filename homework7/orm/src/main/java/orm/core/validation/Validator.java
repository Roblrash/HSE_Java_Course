package orm.core.validation;

import orm.annotation.validation.Constraint;
import orm.core.OrmException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Validator {
    public List<Violation> validate(Object target) {
        if (target == null) {
            throw new OrmException("Object for validation must not be null");
        }

        List<Violation> violations = new ArrayList<>();
        for (Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = readField(field, target);
            for (Annotation annotation : field.getAnnotations()) {
                Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
                if (constraint == null) {
                    continue;
                }
                ConstraintValidator<Annotation, Object> validator = createValidator(constraint);
                boolean valid = validator.isValid(value, annotation);
                if (!valid) {
                    violations.add(new Violation(
                            field.getName(),
                            value,
                            message(annotation),
                            annotation.annotationType()
                    ));
                }
            }
        }
        return violations;
    }

    private Object readField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new OrmException("Cannot read field for validation: " + field.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private ConstraintValidator<Annotation, Object> createValidator(Constraint constraint) {
        try {
            return (ConstraintValidator<Annotation, Object>) constraint.validatedBy().getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new OrmException("Cannot create constraint validator: " + constraint.validatedBy().getName(), e);
        }
    }

    private String message(Annotation annotation) {
        try {
            return (String) annotation.annotationType().getMethod("message").invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new OrmException("Validation annotation must have message() method: "
                    + annotation.annotationType().getName(), e);
        }
    }
}
