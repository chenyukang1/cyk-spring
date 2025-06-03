package com.cyk.spring.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The class PreparedStatementCreator
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
@FunctionalInterface
public interface PreparedStatementCreator {

    PreparedStatement createPreparedStatement(Connection con) throws SQLException;
}
