/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
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

/**
 * The class JdbcTemplate
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
public class JdbcTemplate {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

    private DataSource dataSource;

    private final RowMapper<Boolean> booleanRowMapper = new BasicDataTypeMapper<>();
    private final RowMapper<Integer> integerRowMapper = new BasicDataTypeMapper<>();
    private final RowMapper<Float> floatRowMapper = new BasicDataTypeMapper<>();
    private final RowMapper<Long> longRowMapper = new BasicDataTypeMapper<>();
    private final RowMapper<Double> doubleRowMapper = new BasicDataTypeMapper<>();
    private final RowMapper<String> stringRowMapper = new BasicDataTypeMapper<>();

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

    @SuppressWarnings("unchecked")
    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... args) throws DataAccessException {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (List<T>) queryForList(sql, booleanRowMapper, args);
        } else if (clazz == Integer.class || clazz == int.class) {
            return (List<T>) queryForList(sql, integerRowMapper, args);
        } else if (clazz == Float.class || clazz == float.class) {
            return (List<T>) queryForList(sql, floatRowMapper, args);
        } else if (clazz == Long.class || clazz == long.class) {
            return (List<T>) queryForList(sql, longRowMapper, args);
        } else if (clazz == Double.class || clazz == double.class) {
            return (List<T>) queryForList(sql, doubleRowMapper, args);
        } else if (clazz == String.class) {
            return (List<T>) queryForList(sql, stringRowMapper, args);
        }
        return queryForList(sql, new BeanRowMapper<>(clazz), args);
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

    @SuppressWarnings("unchecked")
    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) queryForObject(sql, booleanRowMapper, args);
        } else if (clazz == Integer.class || clazz == int.class) {
            return (T) queryForObject(sql, integerRowMapper, args);
        } else if (clazz == Float.class || clazz == float.class) {
            return (T) queryForObject(sql, floatRowMapper, args);
        } else if (clazz == Long.class || clazz == long.class) {
            return (T) queryForObject(sql, longRowMapper, args);
        } else if (clazz == Double.class || clazz == double.class) {
            return (T) queryForObject(sql, doubleRowMapper, args);
        } else if (clazz == String.class) {
            return (T) queryForObject(sql, stringRowMapper, args);
        }
        return queryForObject(sql, new BeanRowMapper<>(clazz), args);
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
