package com.cyk.spring.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * The interface StatementCallback
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
@FunctionalInterface
public interface StatementCallback<T> {

    T doInStatement(Statement statement) throws SQLException;
}
