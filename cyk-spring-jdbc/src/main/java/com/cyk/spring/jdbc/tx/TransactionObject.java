package com.cyk.spring.jdbc.tx;

import com.cyk.spring.jdbc.exception.DataAccessException;

import java.sql.Connection;

/**
 * The class TransactionObject
 *
 * @author yukang.chen
 * @date 2025/6/6
 */
public class TransactionObject {

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    private boolean isTransactionActive;

    private long timeoutInSeconds;

    public Connection getConnection() {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            throw new DataAccessException("No connection set");
        }
        return connection;
    }

    public void releaseConnection() {
        connectionHolder.remove();;
    }

    public void setConnection(Connection connection) {
        connectionHolder.set(connection);
    }

    public boolean isTransactionActive() {
        return isTransactionActive;
    }

    public void setTransactionActive(boolean transactionActive) {
        isTransactionActive = transactionActive;
    }

    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }
}
