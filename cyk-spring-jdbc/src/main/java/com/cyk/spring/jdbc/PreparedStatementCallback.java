package com.cyk.spring.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The interface PreparedStatementCallback
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
@FunctionalInterface
public interface PreparedStatementCallback<T> {

    T doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException;
}
