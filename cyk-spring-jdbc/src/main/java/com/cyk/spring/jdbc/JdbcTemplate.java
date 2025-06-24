package com.cyk.spring.jdbc;

import com.cyk.spring.jdbc.exception.DataAccessException;
import com.cyk.spring.jdbc.mapper.BasicDataTypeMapper;
import com.cyk.spring.jdbc.mapper.BeanRowMapper;
import com.cyk.spring.jdbc.tx.TransactionInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class JdbcTemplate
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
public class JdbcTemplate {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

    private DataSource dataSource;

    public JdbcTemplate() {
    }

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T execute(ConnectionCallback<T> callback) {
        Connection current = TransactionInvocationHandler.getConnection();
        if (current != null) {
            try {
                return callback.doInConnection(current);
            } catch (Exception e) {
                throw new DataAccessException(e);
            }
        }

        assert dataSource != null;
        try (Connection newConn = dataSource.getConnection()) {
            return callback.doInConnection(newConn);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> T execute(StatementCallback<T> callback) {
        Connection current = TransactionInvocationHandler.getConnection();
        Statement statement = null;
        if (current != null) {
            try {
                statement = current.createStatement();
                return callback.doInStatement(statement);
            } catch (SQLException e) {
                releaseConnection(null, statement);
                throw new DataAccessException(e);
            }
        }

        assert dataSource != null;
        try (Connection newConn = TransactionInvocationHandler.getConnection();
             Statement stmt = newConn.createStatement()) {
            return callback.doInStatement(stmt);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> callback) {
        Connection current = TransactionInvocationHandler.getConnection();
        PreparedStatement ptmt = null;
        if (current != null) {
            try {
                ptmt = psc.createPreparedStatement(current);
                return callback.doInPreparedStatement(ptmt);
            } catch (SQLException e) {
                releaseConnection(null, ptmt);
                throw new DataAccessException(e);
            }
        }

        assert dataSource != null;
        // 当前事务中没有，创建新连接
        try (Connection newConn = dataSource.getConnection();
             PreparedStatement pstmt = psc.createPreparedStatement(newConn)) {
            if (!newConn.getAutoCommit()) {
                newConn.setAutoCommit(true);
            }
            T result = callback.doInPreparedStatement(pstmt);
            if (!newConn.getAutoCommit()) {
                newConn.setAutoCommit(false);
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("JDBC Connection {} close fail", conn, ex);
            }
        }
    }

    private void releaseConnection(Connection conn, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                logger.error("JDBC Statement {} close fail", stmt, ex);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("JDBC Connection {} close fail", conn, ex);
            }
        }
    }

    private void releaseConnection(Connection conn, PreparedStatement ptmt) {
        if (ptmt != null) {
            try {
                ptmt.close();
            } catch (SQLException ex) {
                logger.error("JDBC Statement {} close fail", ptmt, ex);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("JDBC Connection {} close fail", conn, ex);
            }
        }
    }

    public int update(String sql, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), PreparedStatement::executeUpdate);
    }

    public Number updateAndReturnGeneratedKey(String sql, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), ptmt -> {
            logger.info("execute");
            ptmt.executeUpdate();
            try (ResultSet rs = ptmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return (Number) rs.getObject(1);
                }
            } catch (SQLException e) {
                throw new DataAccessException("JDBC Statement execute fail");
            }
            throw new DataAccessException("Should not reach here");
        });
    }

    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... args) throws DataAccessException {
        BasicDataTypeMapper<T> rowMapper = BasicDataTypeMapper.of(clazz);
        return queryForList(sql, Objects.requireNonNullElseGet(rowMapper, () -> new BeanRowMapper<>(clazz)), args);
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), ptmt -> {
            List<T> list = new ArrayList<>();
            try (ResultSet rs = ptmt.executeQuery()) {
                while (rs.next()) {
                    T t = rowMapper.mapRow(rs, rs.getRow());
                    if (t != null) {
                        list.add(t);
                    }
                }
            } catch (SQLException e) {
                logger.error("JDBC Statement execute fail", e);
            }
            return list;
        });
    }

    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
        BasicDataTypeMapper<T> rowMapper = BasicDataTypeMapper.of(clazz);
        return queryForObject(sql, Objects.requireNonNullElseGet(rowMapper, () -> new BeanRowMapper<>(clazz)), args);
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), ptmt -> {
            T t = null;
            try (ResultSet rs = ptmt.executeQuery()) {
                while (rs.next()) {
                    if (t == null) {
                        t = rowMapper.mapRow(rs, rs.getRow());
                    } else {
                        throw new DataAccessException("Multiple rows found.");
                    }
                }
                if (t == null) {
                    throw new DataAccessException("Empty result set");
                }
            } catch (SQLException e) {
                logger.error("JDBC Statement execute fail", e);
            }
            return t;
        });
    }

    private PreparedStatementCreator preparedStatementCreator(String sql, Object... args) {
        return conn -> {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ptmt.setObject(i + 1, args[i]);
            }
            return ptmt;
        };
    }
}
