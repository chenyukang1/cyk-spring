package com.cyk.spring.jdbc.tx;

import com.cyk.spring.jdbc.exception.DataAccessException;
import com.cyk.spring.jdbc.exception.IllegalTransactionStateException;
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
            DefaultTransactionStatus status = new DefaultTransactionStatus(txObject, true);
            try {
                // 由 JDBC 驱动自动在需要时发出事务开始语句
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
                return status;
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
        }
        throw new TransactionException("propagation " + definition.getPropagationBehavior() + " not supported");
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        if (status.isRollbackOnly()) {
            processRollback(status);
        } else {
            processCommit(status);
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalTransactionStateException(
                    "Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        processRollback(status);
    }

    private void processRollback(TransactionStatus status) {
        DefaultTransactionStatus defaultStatus = (DefaultTransactionStatus) status;
        TransactionObject txObject = defaultStatus.getTransaction();
        Connection conn = txObject.getConnection();
        try {
            conn.rollback();
            conn.close();
            txObject.releaseConnection();
        } catch (SQLException e) {
            throw new TransactionException("rollback transaction fail", e);
        } finally {
            defaultStatus.setCompleted();
        }
    }

    private void processCommit(TransactionStatus status) {
        DefaultTransactionStatus defaultStatus = (DefaultTransactionStatus) status;
        TransactionObject txObject = defaultStatus.getTransaction();
        Connection conn = txObject.getConnection();
        try {
            conn.commit();
            conn.close();
            txObject.releaseConnection();
        } catch (SQLException e) {
            throw new TransactionException("commit transaction fail", e);
        } finally {
            defaultStatus.setCompleted();
        }
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
