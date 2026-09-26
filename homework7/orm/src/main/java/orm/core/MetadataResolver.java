package orm.core;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MetadataResolver {
    private final Map<Class<?>, EntityMetadata> cache = new ConcurrentHashMap<>();

    public EntityMetadata resolve(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, this::parse);
    }

    private EntityMetadata parse(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new OrmException("Class is not annotated with @Table: " + clazz.getName());
        }

        String tableName = table.name().isBlank()
                ? clazz.getSimpleName().toLowerCase(Locale.ROOT)
                : table.name();
        validateIdentifier(tableName, "table");

        FieldMetadata idField = null;
        List<FieldMetadata> regularFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            Id id = field.getAnnotation(Id.class);
            Column column = field.getAnnotation(Column.class);
            if (id == null && column == null) {
                continue;
            }

            String columnName = column != null && !column.name().isBlank() ? column.name() : field.getName();
            validateIdentifier(columnName, "column");

            if (id != null) {
                if (idField != null) {
                    throw new OrmException("Entity has more than one @Id field: " + clazz.getName());
                }
                if (field.getType() != Long.class && field.getType() != long.class) {
                    throw new OrmException("@Id field must be Long or long: " + field.getName());
                }
                idField = new FieldMetadata(field, columnName, true, false);
            } else {
                boolean nullable = column == null || column.nullable();
                regularFields.add(new FieldMetadata(field, columnName, false, nullable));
            }
        }

        if (idField == null) {
            throw new OrmException("Entity has no @Id field: " + clazz.getName());
        }

        return new EntityMetadata(clazz, tableName, idField, regularFields);
    }

    private void validateIdentifier(String identifier, String kind) {
        if (!identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new OrmException("Invalid SQL " + kind + " identifier: " + identifier);
        }
    }
}
