package com.cyk.spring.jdbc.tx;

import com.cyk.spring.jdbc.exception.DataAccessException;
import com.cyk.spring.jdbc.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class DefaultPlatformTransactionManager
 *
 * @author yukang.chen
 * @date 2025/6/5
 */
public class DataSourceTransactionManager implements PlatformTransactionManager {

    private static final Logger log = LoggerFactory.getLogger(DataSourceTransactionManager.class);
    private DataSource dataSource;
    private static final ThreadLocal<TransactionStatus> transactionStatus = new ThreadLocal<>();

    public DataSourceTransactionManager() {
    }

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        TransactionObject txObject = new TransactionObject();
        Connection conn;
        try {
            conn = obtainDataSource().getConnection();
        } catch (SQLException e) {
            throw new TransactionException("get connection fail", e);
        }
        txObject.setConnection(conn);
        // 检查事务超时
        if (definition.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
            throw new TransactionException("Invalid transaction timeout " + definition.getTimeout());
        }

        // 检查事务传播级别
        if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
                definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
                definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
            // 开启事务
            try {
                if (conn.getAutoCommit()) {
                    conn.setAutoCommit(false);
                }
                if (definition.isReadOnly()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("SET TRANSACTION READ ONLY");
                    }
                }
                txObject.setTransactionActive(true);
                int timeout = definition.getTimeout();
                if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
                    txObject.setTimeoutInSeconds(timeout);
                }
            } catch (Exception e) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                         log.error("JDBC Connection {} close fail", conn, ex);
                    }
                }
                throw new TransactionException("begin transaction fail", e);
            }


        } else throw new TransactionException("propagation " + definition.getPropagationBehavior() + " not supported");

        return null;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {

    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {

    }

    void doBegin() {

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource obtainDataSource() {
        DataSource dataSource = getDataSource();
        if (dataSource == null) {
            throw new DataAccessException("no datasource set!");
        }
        return dataSource;
    }

}
