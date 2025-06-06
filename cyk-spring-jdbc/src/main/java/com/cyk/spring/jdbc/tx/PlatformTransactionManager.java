package com.cyk.spring.jdbc.tx;

import com.cyk.spring.jdbc.exception.IllegalTransactionStateException;
import com.cyk.spring.jdbc.exception.TransactionException;

/**
 * The interface PlatformTransactionManager
 *
 * @author yukang.chen
 * @date 2025/5/27
 */
public interface PlatformTransactionManager extends TransactionManager {

    /**
     * Return a currently active transaction or create a new one, according to
     * the specified propagation behavior.
     * <p>Note that parameters like isolation level or timeout will only be applied
     * to new transactions, and thus be ignored when participating in active ones.
     * <p>Furthermore, not all transaction definition settings will be supported
     * by every transaction manager: A proper transaction manager implementation
     * should throw an exception when unsupported settings are encountered.
     * <p>An exception to the above rule is the read-only flag, which should be
     * ignored if no explicit read-only mode is supported. Essentially, the
     * read-only flag is just a hint for potential optimization.
     * @param definition the TransactionDefinition instance (can be {@code null} for defaults),
     * describing propagation behavior, isolation level, timeout etc.
     * @return transaction status object representing the new or current transaction
     * @throws TransactionException in case of lookup, creation, or system errors
     * @throws IllegalTransactionStateException if the given transaction definition
     * cannot be executed (for example, if a currently active transaction is in
     * conflict with the specified propagation behavior)
     * @see TransactionDefinition#getPropagationBehavior
     * @see TransactionDefinition#getIsolationLevel
     * @see TransactionDefinition#getTimeout
     * @see TransactionDefinition#isReadOnly
     */
    TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException;

    /**
     * Commit the given transaction, with regard to its status. If the transaction
     * has been marked rollback-only programmatically, perform a rollback.
     * <p>If the transaction wasn't a new one, omit the commit for proper
     * participation in the surrounding transaction. If a previous transaction
     * has been suspended to be able to create a new one, resume the previous
     * transaction after committing the new one.
     * <p>Note that when the commit call completes, no matter if normally or
     * throwing an exception, the transaction must be fully completed and
     * cleaned up. No rollback call should be expected in such a case.
     * <p>Depending on the concrete transaction manager setup, {@code commit}
     * may propagate {@link org.springframework.dao.DataAccessException} as well,
     * either from before-commit flushes or from the actual commit step.
     * @param status object returned by the {@code getTransaction} method
     * @throws UnexpectedRollbackException in case of an unexpected rollback
     * that the transaction coordinator initiated
     * @throws HeuristicCompletionException in case of a transaction failure
     * caused by a heuristic decision on the side of the transaction coordinator
     * @throws TransactionSystemException in case of commit or system errors
     * (typically caused by fundamental resource failures)
     * @throws IllegalTransactionStateException if the given transaction
     * is already completed (that is, committed or rolled back)
     * @see TransactionStatus#setRollbackOnly
     */
    void commit(TransactionStatus status) throws TransactionException;

    /**
     * Perform a rollback of the given transaction.
     * <p>If the transaction wasn't a new one, just set it rollback-only for proper
     * participation in the surrounding transaction. If a previous transaction
     * has been suspended to be able to create a new one, resume the previous
     * transaction after rolling back the new one.
     * <p><b>Do not call rollback on a transaction if commit threw an exception.</b>
     * The transaction will already have been completed and cleaned up when commit
     * returns, even in case of a commit exception. Consequently, a rollback call
     * after commit failure will lead to an IllegalTransactionStateException.
     * <p>Depending on the concrete transaction manager setup, {@code rollback}
     * may propagate {@link org.springframework.dao.DataAccessException} as well.
     * @param status object returned by the {@code getTransaction} method
     * @throws TransactionSystemException in case of rollback or system errors
     * (typically caused by fundamental resource failures)
     * @throws IllegalTransactionStateException if the given transaction
     * is already completed (that is, committed or rolled back)
     */
    void rollback(TransactionStatus status) throws TransactionException;
}
