package orm.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public final class EntityMetadata {
    private final Class<?> entityClass;
    private final String tableName;
    private final FieldMetadata idField;
    private final List<FieldMetadata> regularFields;
    private final List<FieldMetadata> allFields;

    EntityMetadata(Class<?> entityClass, String tableName, FieldMetadata idField, List<FieldMetadata> regularFields) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.idField = idField;
        this.regularFields = Collections.unmodifiableList(new ArrayList<>(regularFields));

        List<FieldMetadata> fields = new ArrayList<>();
        fields.add(idField);
        fields.addAll(regularFields);
        this.allFields = Collections.unmodifiableList(fields);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public FieldMetadata getIdField() {
        return idField;
    }

    public List<FieldMetadata> getRegularFields() {
        return regularFields;
    }

    public List<FieldMetadata> getAllFields() {
        return allFields;
    }

    public Optional<FieldMetadata> findField(String fieldName) {
        return allFields.stream()
                .filter(field -> field.getFieldName().equals(fieldName))
                .findFirst();
    }

    public String joinedRegularColumnNames() {
        return joinColumnNames(regularFields);
    }

    public String joinedAllColumnNames() {
        return joinColumnNames(allFields);
    }

    public String placeholdersForRegularFields() {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < regularFields.size(); i++) {
            joiner.add("?");
        }
        return joiner.toString();
    }

    public String updateAssignments() {
        StringJoiner joiner = new StringJoiner(", ");
        for (FieldMetadata field : regularFields) {
            joiner.add(field.getColumnName() + " = ?");
        }
        return joiner.toString();
    }

    public Object newInstance() {
        try {
            Constructor<?> constructor = entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new OrmException("Class must have a no-argument constructor: " + entityClass.getName(), e);
        }
    }

    private String joinColumnNames(List<FieldMetadata> fields) {
        StringJoiner joiner = new StringJoiner(", ");
        for (FieldMetadata field : fields) {
            joiner.add(field.getColumnName());
        }
        return joiner.toString();
    }
}
