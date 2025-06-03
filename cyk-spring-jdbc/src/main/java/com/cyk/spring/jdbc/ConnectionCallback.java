package com.cyk.spring.jdbc;

import java.sql.Connection;

/**
 * The interface ConnectionCallback
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
@FunctionalInterface
public interface ConnectionCallback<T> {

    T doInConnection(Connection connection);
}
