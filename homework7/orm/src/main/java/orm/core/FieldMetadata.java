package orm.core;

import java.lang.reflect.Field;
import java.time.LocalDate;

public final class FieldMetadata {
    private final Field field;
    private final String columnName;
    private final boolean id;
    private final boolean nullable;

    FieldMetadata(Field field, String columnName, boolean id, boolean nullable) {
        this.field = field;
        this.columnName = columnName;
        this.id = id;
        this.nullable = nullable;
        this.field.setAccessible(true);
    }

    public String getFieldName() {
        return field.getName();
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isId() {
        return id;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Object getValue(Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new OrmException("Cannot read field " + field.getName(), e);
        }
    }

    public void setValue(Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new OrmException("Cannot write field " + field.getName(), e);
        }
    }

    public String getSqlType() {
        Class<?> type = field.getType();
        if (type == Long.class || type == long.class) {
            return "BIGINT";
        }
        if (type == Integer.class || type == int.class) {
            return "INT";
        }
        if (type == Double.class || type == double.class) {
            return "DOUBLE";
        }
        if (type == Boolean.class || type == boolean.class) {
            return "BOOLEAN";
        }
        if (type == String.class) {
            return "VARCHAR(255)";
        }
        if (type == LocalDate.class) {
            return "DATE";
        }
        throw new OrmException("Unsupported field type: " + type.getName());
    }
}
