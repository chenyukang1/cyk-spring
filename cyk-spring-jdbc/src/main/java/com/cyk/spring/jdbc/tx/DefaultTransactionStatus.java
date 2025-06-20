package com.cyk.spring.jdbc.tx;

/**
 * The class DefaultTransactionStatus
 *
 * @author yukang.chen
 * @date 2025/6/6
 */
public class DefaultTransactionStatus implements TransactionStatus {

    private final TransactionObject transaction;

    private final boolean newTransaction;

    private boolean rollbackOnly = false;

    private boolean completed = false;

    public DefaultTransactionStatus(TransactionObject transaction, boolean newTransaction) {
        this.transaction = transaction;
        this.newTransaction = newTransaction;
    }

    @Override
    public boolean isNewTransaction() {
        return (hasTransaction() && this.newTransaction);
    }

    @Override
    public void setRollbackOnly() {
        if (this.completed) {
            throw new IllegalStateException("Transaction completed");
        }
        this.rollbackOnly = true;
    }

    @Override
    public boolean isRollbackOnly() {
        return this.rollbackOnly;
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    /**
     * Mark this transaction as completed, that is, committed or rolled back.
     */
    public void setCompleted() {
        this.completed = true;
    }

    public TransactionObject getTransaction() {
        return transaction;
    }

    public boolean hasTransaction() {
        return this.transaction != null;
    }
}
