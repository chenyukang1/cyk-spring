package com.cyk.spring.jdbc.mapper;

import com.cyk.spring.jdbc.RowMapper;
import com.cyk.spring.jdbc.exception.DataAccessException;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * The class BeanRowMapper
 *
 * @author yukang.chen
 * @date 2025/5/30
 */
public class BeanRowMapper<T> implements RowMapper<T> {

    private static Logger logger = LoggerFactory.getLogger(BeanRowMapper.class);

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Map<String, Field> fieldMap = new HashMap<>();
    private final Map<String, Method> methodMap = new HashMap<>();

    public BeanRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        try {
            constructor = clazz.getConstructor();
            for (Field field : clazz.getFields()) {
                fieldMap.put(field.getName(), field);
            }
            for (Method method : clazz.getMethods()) {
                if (method.getParameters().length == 1 &&
                        method.getName().length() > 3 &&
                        method.getName().startsWith("set")) {
                    methodMap.put(method.getName(), method);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new DataAccessException("No public constructor found in class " + clazz.getName());
        }

    }

    @Nullable
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T bean;
        try {
            bean = constructor.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String label = metaData.getColumnLabel(i);
                String setMethodName = "set" + label.substring(0, 1).toUpperCase() + label.substring(1);
                Method method = methodMap.get(setMethodName);
                if (method != null) {
                    method.invoke(bean, rs.getObject(label));
                } else {
                    Field field = fieldMap.get(label);
                    if (field != null) {
                        field.set(bean, rs.getObject(label));
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataAccessException("Instantiation or access error in class " + clazz.getName());
        }

        return bean;
    }
}
