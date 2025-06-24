package com.cyk.spring.jdbc.tx;

import java.util.Map;

/**
 * The class DefaultTransactionDefinition
 *
 * @author yukang.chen
 * @date 2025/6/5
 */
public class DefaultTransactionDefinition implements TransactionDefinition {

    static final Map<String, Integer> propagationConstants = Map.of(
            "PROPAGATION_REQUIRED", TransactionDefinition.PROPAGATION_REQUIRED,
            "PROPAGATION_SUPPORTS", TransactionDefinition.PROPAGATION_SUPPORTS,
            "PROPAGATION_MANDATORY", TransactionDefinition.PROPAGATION_MANDATORY,
            "PROPAGATION_REQUIRES_NEW", TransactionDefinition.PROPAGATION_REQUIRES_NEW,
            "PROPAGATION_NOT_SUPPORTED", TransactionDefinition.PROPAGATION_NOT_SUPPORTED,
            "PROPAGATION_NEVER", TransactionDefinition.PROPAGATION_NEVER,
            "PROPAGATION_NESTED", TransactionDefinition.PROPAGATION_NESTED
    );

    static final Map<String, Integer> isolationConstants = Map.of(
            "ISOLATION_DEFAULT", TransactionDefinition.ISOLATION_DEFAULT,
            "ISOLATION_READ_UNCOMMITTED", TransactionDefinition.ISOLATION_READ_UNCOMMITTED,
            "ISOLATION_READ_COMMITTED", TransactionDefinition.ISOLATION_READ_COMMITTED,
            "ISOLATION_REPEATABLE_READ", TransactionDefinition.ISOLATION_REPEATABLE_READ,
            "ISOLATION_SERIALIZABLE", TransactionDefinition.ISOLATION_SERIALIZABLE
    );

    private int propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED;

    private int isolationLevel = TransactionDefinition.ISOLATION_DEFAULT;

    private boolean readOnly = false;

    private int timeout = -1;

    private Class<? extends Throwable>[] rollbackFor;

    @Override
    public int getPropagationBehavior() {
        return propagationBehavior;
    }

    public void setPropagationBehavior(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    public void setPropagationBehavior(Propagation propagation) {
        if (propagation == null) {
            throw new IllegalArgumentException("'propagation' cannot be null");
        }
        Integer propagationBehavior = propagationConstants.get("PROPAGATION_" + propagation.name());
        if (propagationBehavior == null) {
            throw new IllegalArgumentException("'propagation' is not a valid propagation behavior");
        }
        setPropagationBehavior(propagationBehavior);
    }

    @Override
    public int getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public void setIsolationLevel(Isolation isolation) {
        if (isolation == null) {
            throw new IllegalArgumentException("'isolation' cannot be null");
        }
        Integer isolationLevel = isolationConstants.get("ISOLATION_" + isolation.name());
        if (isolationLevel == null) {
            throw new IllegalArgumentException("'isolation' is not a valid isolation level");
        }
        setIsolationLevel(isolationLevel);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean rollbackOn(Throwable ex) {
        if (rollbackFor == null) {
            return ex instanceof RuntimeException || ex instanceof Error;
        }
        for (Class<? extends Throwable> rb : rollbackFor) {
            if (rb.isInstance(ex)) {
                return true;
            }
        }
        return false;
    }

    public Class<? extends Throwable>[] getRollbackFor() {
        return rollbackFor;
    }

    public void setRollbackFor(Class<? extends Throwable>[] rollbackFor) {
        this.rollbackFor = rollbackFor;
    }
}
