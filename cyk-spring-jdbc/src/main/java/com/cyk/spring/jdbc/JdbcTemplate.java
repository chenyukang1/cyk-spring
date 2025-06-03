package com.cyk.spring.jdbc;

import com.cyk.spring.jdbc.exception.DataAccessException;
import com.cyk.spring.jdbc.mapper.BasicDataTypeMapper;
import com.cyk.spring.jdbc.mapper.BeanRowMapper;
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
        assert dataSource != null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return callback.doInConnection(connection);
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("JDBC Connection {} close fail", connection, ex);
            }
            connection = null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("JDBC Connection {} close fail", connection, e);
                }
            }
        }
        return null;
    }

    public <T> T execute(StatementCallback<T> callback) {
        assert dataSource != null;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            return callback.doInStatement(stmt);
        } catch (SQLException e) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    logger.error("JDBC Statement {} close fail", stmt, ex);
                }
            }
            stmt = null;
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("JDBC Connection {} close fail", conn, ex);
                }
            }
            conn = null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("JDBC Connection {} close fail", conn, e);
                }
            }
        }
        return null;
    }

    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> callback) {
        assert dataSource != null;
        Connection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = dataSource.getConnection();
            ptmt = psc.createPreparedStatement(conn);
            return callback.doInPreparedStatement(ptmt);
        } catch (SQLException e) {
            logger.error("JDBC Statement execute fail", e);
            if (ptmt != null) {
                try {
                    ptmt.close();
                } catch (SQLException ex) {
                    logger.error("JDBC Statement {} close fail", ptmt, ex);
                }
            }
            ptmt = null;
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("JDBC Connection {} close fail", conn, ex);
                }
            }
            conn = null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("JDBC Connection {} close fail", conn, e);
                }
            }
        }
        return null;
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
