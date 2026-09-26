package orm.repository;

import orm.core.EntityMetadata;
import orm.core.FieldMetadata;
import orm.core.MetadataResolver;
import orm.core.OrmException;
import orm.core.validation.ValidationException;
import orm.core.validation.Validator;
import orm.core.validation.Violation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class EntityManager {
    private final Connection connection;
    private final MetadataResolver metadataResolver = new MetadataResolver();
    private final Validator validator = new Validator();

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    public void createTable(Class<?> clazz) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSql(metadata));
        } catch (SQLException e) {
            throw new OrmException("Cannot create table for " + clazz.getName(), e);
        }
    }

    public Long save(Object entity) {
        requireEntity(entity);
        validate(entity);

        EntityMetadata metadata = metadataResolver.resolve(entity.getClass());
        try (PreparedStatement statement = connection.prepareStatement(insertSql(metadata), Statement.RETURN_GENERATED_KEYS)) {
            fillRegularFieldParameters(statement, metadata, entity, 1);
            statement.executeUpdate();
            Long id = readGeneratedId(statement);
            metadata.getIdField().setValue(entity, id);
            return id;
        } catch (SQLException e) {
            throw new OrmException("Cannot save entity " + entity.getClass().getName(), e);
        }
    }

    public <T> Optional<T> findById(Class<T> clazz, Long id) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        String sql = "SELECT " + metadata.joinedAllColumnNames()
                + " FROM " + metadata.getTableName()
                + " WHERE " + metadata.getIdField().getColumnName() + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(clazz.cast(mapRow(metadata, resultSet)));
            }
        } catch (SQLException e) {
            throw new OrmException("Cannot find entity by id", e);
        }
    }

    public <T> List<T> findAll(Class<T> clazz) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        String sql = "SELECT " + metadata.joinedAllColumnNames() + " FROM " + metadata.getTableName();
        return query(clazz, metadata, sql, statement -> {
        });
    }

    public int update(Object entity) {
        requireEntity(entity);
        validate(entity);

        EntityMetadata metadata = metadataResolver.resolve(entity.getClass());
        Object id = metadata.getIdField().getValue(entity);
        if (id == null) {
            throw new OrmException("Cannot update entity with null id");
        }

        String sql = "UPDATE " + metadata.getTableName()
                + " SET " + metadata.updateAssignments()
                + " WHERE " + metadata.getIdField().getColumnName() + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = fillRegularFieldParameters(statement, metadata, entity, 1);
            statement.setObject(index, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Cannot update entity " + entity.getClass().getName(), e);
        }
    }

    public int delete(Object entity) {
        requireEntity(entity);
        EntityMetadata metadata = metadataResolver.resolve(entity.getClass());
        Object id = metadata.getIdField().getValue(entity);
        if (id == null) {
            throw new OrmException("Cannot delete entity with null id");
        }
        return deleteById(entity.getClass(), ((Number) id).longValue());
    }

    public int deleteById(Class<?> clazz, Long id) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        String sql = "DELETE FROM " + metadata.getTableName()
                + " WHERE " + metadata.getIdField().getColumnName() + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Cannot delete entity by id", e);
        }
    }

    public void saveAll(List<?> entities) {
        if (entities == null) {
            throw new OrmException("Entity list must not be null");
        }
        if (entities.isEmpty()) {
            return;
        }

        Class<?> entityClass = null;
        for (Object entity : entities) {
            requireEntity(entity);
            if (entityClass == null) {
                entityClass = entity.getClass();
            } else if (entity.getClass() != entityClass) {
                throw new OrmException("All batch entities must have the same class");
            }
            validate(entity);
        }

        EntityMetadata metadata = metadataResolver.resolve(entityClass);
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(insertSql(metadata), Statement.RETURN_GENERATED_KEYS)) {
                for (Object entity : entities) {
                    fillRegularFieldParameters(statement, metadata, entity, 1);
                    statement.addBatch();
                }
                statement.executeBatch();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    for (Object entity : entities) {
                        if (keys.next()) {
                            metadata.getIdField().setValue(entity, keys.getLong(1));
                        }
                    }
                }
            }
            connection.commit();
        } catch (SQLException | RuntimeException e) {
            rollbackQuietly();
            if (e instanceof OrmException) {
                throw (OrmException) e;
            }
            throw new OrmException("Cannot save batch", e);
        } finally {
            restoreAutoCommit(oldAutoCommit);
        }
    }

    public <T> List<T> findAllWhere(Class<T> clazz, String fieldName, Object value) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        FieldMetadata field = requireSearchField(metadata, fieldName, value);
        String sql = "SELECT " + metadata.joinedAllColumnNames()
                + " FROM " + metadata.getTableName()
                + " WHERE " + field.getColumnName() + " = ?";
        return query(clazz, metadata, sql, statement -> setParameter(statement, 1, value));
    }

    public <T> Optional<T> findOneWhere(Class<T> clazz, String fieldName, Object value) {
        List<T> found = findAllWhere(clazz, fieldName, value);
        if (found.size() > 1) {
            throw new OrmException("More than one row found for field " + fieldName);
        }
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    public long count(Class<?> clazz) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        String sql = "SELECT COUNT(*) FROM " + metadata.getTableName();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new OrmException("Cannot count rows", e);
        }
    }

    public boolean existsById(Class<?> clazz, Long id) {
        EntityMetadata metadata = metadataResolver.resolve(clazz);
        String sql = "SELECT 1 FROM " + metadata.getTableName()
                + " WHERE " + metadata.getIdField().getColumnName() + " = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new OrmException("Cannot check entity existence", e);
        }
    }

    private String createTableSql(EntityMetadata metadata) {
        StringJoiner columns = new StringJoiner(", ");
        columns.add(metadata.getIdField().getColumnName() + " BIGINT AUTO_INCREMENT PRIMARY KEY");
        for (FieldMetadata field : metadata.getRegularFields()) {
            String nullable = field.isNullable() ? "" : " NOT NULL";
            columns.add(field.getColumnName() + " " + field.getSqlType() + nullable);
        }
        return "CREATE TABLE IF NOT EXISTS " + metadata.getTableName() + " (" + columns + ")";
    }

    private String insertSql(EntityMetadata metadata) {
        if (metadata.getRegularFields().isEmpty()) {
            return "INSERT INTO " + metadata.getTableName() + " DEFAULT VALUES";
        }
        return "INSERT INTO " + metadata.getTableName()
                + " (" + metadata.joinedRegularColumnNames() + ") VALUES ("
                + metadata.placeholdersForRegularFields() + ")";
    }

    private Long readGeneratedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new OrmException("Database did not return generated id");
            }
            return keys.getLong(1);
        }
    }

    private int fillRegularFieldParameters(PreparedStatement statement, EntityMetadata metadata, Object entity, int startIndex)
            throws SQLException {
        int index = startIndex;
        for (FieldMetadata field : metadata.getRegularFields()) {
            Object value = field.getValue(entity);
            if (!field.isNullable() && value == null) {
                throw new OrmException("Field must not be null: " + field.getFieldName());
            }
            setParameter(statement, index++, value);
        }
        return index;
    }

    private void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.NULL);
        } else if (value instanceof LocalDate) {
            statement.setDate(index, Date.valueOf((LocalDate) value));
        } else {
            statement.setObject(index, value);
        }
    }

    private Object mapRow(EntityMetadata metadata, ResultSet resultSet) throws SQLException {
        Object entity = metadata.newInstance();
        for (FieldMetadata field : metadata.getAllFields()) {
            field.setValue(entity, readColumn(resultSet, field));
        }
        return entity;
    }

    private Object readColumn(ResultSet resultSet, FieldMetadata field) throws SQLException {
        Class<?> type = field.getType();
        String column = field.getColumnName();
        if (type == LocalDate.class) {
            Date date = resultSet.getDate(column);
            return date == null ? null : date.toLocalDate();
        }

        Object value = resultSet.getObject(column);
        if (value == null) {
            return null;
        }
        if (type == Long.class || type == long.class) {
            return ((Number) value).longValue();
        }
        if (type == Integer.class || type == int.class) {
            return ((Number) value).intValue();
        }
        if (type == Double.class || type == double.class) {
            return ((Number) value).doubleValue();
        }
        if (type == Boolean.class || type == boolean.class) {
            return value instanceof Boolean ? value : Boolean.valueOf(value.toString());
        }
        return value;
    }

    private <T> List<T> query(Class<T> clazz, EntityMetadata metadata, String sql, StatementBinder binder) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(clazz.cast(mapRow(metadata, resultSet)));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new OrmException("Cannot execute query", e);
        }
    }

    private FieldMetadata requireSearchField(EntityMetadata metadata, String fieldName, Object value) {
        FieldMetadata field = metadata.findField(fieldName)
                .orElseThrow(() -> new OrmException("Unknown field: " + fieldName));
        if (value != null && !wrap(field.getType()).isInstance(value)) {
            throw new OrmException("Value type does not match field " + fieldName);
        }
        return field;
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        return type;
    }

    private void validate(Object entity) {
        List<Violation> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void requireEntity(Object entity) {
        if (entity == null) {
            throw new OrmException("Entity must not be null");
        }
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void restoreAutoCommit(boolean oldAutoCommit) {
        try {
            connection.setAutoCommit(oldAutoCommit);
        } catch (SQLException e) {
            throw new OrmException("Cannot restore autocommit", e);
        }
    }

    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
