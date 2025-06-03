package com.cyk.spring.jdbc.mapper;

import com.cyk.spring.jdbc.RowMapper;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The class BasicDataTypeMapper
 *
 * @author yukang.chen
 * @date 2025/5/30
 */
public class BasicDataTypeMapper<T> implements RowMapper<T> {

    private static final BasicDataTypeMapper<Boolean> BOOLEAN_ROW_MAPPER = new BasicDataTypeMapper<>();
    private static final BasicDataTypeMapper<Integer> INTEGER_ROW_MAPPER = new BasicDataTypeMapper<>();
    private static final BasicDataTypeMapper<Float> FLOAT_ROW_MAPPER = new BasicDataTypeMapper<>();
    private static final BasicDataTypeMapper<Long> LONG_ROW_MAPPER = new BasicDataTypeMapper<>();
    private static final BasicDataTypeMapper<Double> DOUBLE_ROW_MAPPER = new BasicDataTypeMapper<>();
    private static final BasicDataTypeMapper<String> STRING_ROW_MAPPER = new BasicDataTypeMapper<>();

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return (T) rs.getObject(1);
    }

    public static <T> BasicDataTypeMapper<T> of(Class<T> clazz) {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (BasicDataTypeMapper<T>) ofBoolean();
        } else if (clazz == Integer.class || clazz == int.class) {
            return (BasicDataTypeMapper<T>) ofInteger();
        } else if (clazz == Float.class || clazz == float.class) {
            return (BasicDataTypeMapper<T>) ofFloat();
        } else if (clazz == Long.class || clazz == long.class) {
            return (BasicDataTypeMapper<T>) ofLong();
        } else if (clazz == Double.class || clazz == double.class) {
            return (BasicDataTypeMapper<T>) ofDouble();
        } else if (clazz == String.class) {
            return (BasicDataTypeMapper<T>) ofString();
        }
        return null;
    }

    public static BasicDataTypeMapper<Boolean> ofBoolean() {
        return BOOLEAN_ROW_MAPPER;
    }

    public static BasicDataTypeMapper<Integer> ofInteger() {
        return INTEGER_ROW_MAPPER;
    }

    public static BasicDataTypeMapper<Float> ofFloat() {
        return FLOAT_ROW_MAPPER;
    }

    public static BasicDataTypeMapper<Long> ofLong() {
        return LONG_ROW_MAPPER;
    }

    public static BasicDataTypeMapper<Double> ofDouble() {
        return DOUBLE_ROW_MAPPER;
    }

    public static BasicDataTypeMapper<String> ofString() {
        return STRING_ROW_MAPPER;
    }

}
