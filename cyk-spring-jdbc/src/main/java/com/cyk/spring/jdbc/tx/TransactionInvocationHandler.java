package com.cyk.spring.jdbc.tx;

import com.cyk.spring.jdbc.exception.TransactionException;
import com.cyk.spring.jdbc.tx.annotation.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The class TransactionInvocationHandler
 *
 * @author yukang.chen
 * @date 2025/6/5
 */
public class TransactionInvocationHandler implements InvocationHandler {

    private PlatformTransactionManager ptm;

    private static final ThreadLocal<TransactionInfo> transactionInfoHolder = new ThreadLocal<>();

    public TransactionInvocationHandler() {
    }

    public TransactionInvocationHandler(PlatformTransactionManager ptm) {
        this.ptm = ptm;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TransactionInfo txInfo = transactionInfoHolder.get();
        if (txInfo == null) {
            try {
                Transactional transactional = method.getAnnotatedReturnType().getAnnotation(Transactional.class);
                TransactionDefinition definition = determinTransactionDefinition(transactional);
                TransactionInfo newInfo = createTransactionIfNecessary(ptm, definition);
                transactionInfoHolder.set(newInfo);

                Object res = null;
                try {
                    res = method.invoke(proxy, args);
                } catch (Throwable e) {
                    if (newInfo != null && newInfo.getTransactionStatus() != null) {
                        if (definition.rollbackOn(e)) {
                            newInfo.getTransactionManager().rollback(newInfo.getTransactionStatus());
                        } else {
                            newInfo.getTransactionManager().commit(newInfo.getTransactionStatus());
                        }
                        throw e;
                    }
                } finally {
                    if (newInfo != null) {
                        newInfo.restoreThreadLocalStatus();
                    }
                }

                if (res != null && newInfo.getTransactionStatus() != null) {
                    TransactionStatus status = newInfo.getTransactionStatus();
                    if (res instanceof Future<?> future && future.isDone()) {
                        try {
                            future.get();
                        } catch (ExecutionException ex) {
                            Throwable cause = ex.getCause();
                            if (cause != null && definition.rollbackOn(cause)) {
                                status.setRollbackOnly();
                            }
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                if (newInfo != null && newInfo.getTransactionStatus() != null) {
                    newInfo.getTransactionManager().commit(newInfo.getTransactionStatus());
                }
                return res;
            } finally {
                transactionInfoHolder.remove();
            }
        } else {
            return method.invoke(proxy, args);
        }
    }

    protected TransactionDefinition determinTransactionDefinition(Transactional transactional) {
        if (transactional == null) {
            throw new TransactionException("Should not null");
        }
        // 获取事务配置
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(transactional.isolation());
        definition.setTimeout(transactional.timeout());
        definition.setPropagationBehavior(transactional.propagation());
        definition.setRollbackFor(transactional.rollbackFor());
        definition.setReadOnly(false);
        return definition;
    }

    private TransactionInfo createTransactionIfNecessary(PlatformTransactionManager ptm, TransactionDefinition definition) {
        TransactionStatus txStatus = ptm.getTransaction(definition);
        TransactionInfo txInfo = new TransactionInfo();
        txInfo.setTransactionStatus(txStatus);
        txInfo.setTransactionDefinition(definition);
        txInfo.setTransactionManager(ptm);
        txInfo.bindToThread();
        return txInfo;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.ptm = transactionManager;
    }

    protected static final class TransactionInfo {

        private TransactionStatus transactionStatus;

        private TransactionDefinition transactionDefinition;

        private PlatformTransactionManager transactionManager;

        private TransactionInfo oldTransactionInfo;

        public boolean hasTransaction() {
            return this.transactionStatus != null;
        }

        public void bindToThread() {
            this.oldTransactionInfo = transactionInfoHolder.get();
            transactionInfoHolder.set(this);
        }

        private void restoreThreadLocalStatus() {
            // Use stack to restore old transaction TransactionInfo.
            // Will be null if none was set.
            transactionInfoHolder.set(this.oldTransactionInfo);
        }

        public TransactionStatus getTransactionStatus() {
            return transactionStatus;
        }

        public void setTransactionStatus(TransactionStatus transactionStatus) {
            this.transactionStatus = transactionStatus;
        }

        public TransactionDefinition getTransactionDefinition() {
            return transactionDefinition;
        }

        public void setTransactionDefinition(TransactionDefinition transactionDefinition) {
            this.transactionDefinition = transactionDefinition;
        }

        public PlatformTransactionManager getTransactionManager() {
            return transactionManager;
        }

        public void setTransactionManager(PlatformTransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }

        public TransactionInfo getOldTransactionInfo() {
            return oldTransactionInfo;
        }

        public void setOldTransactionInfo(TransactionInfo oldTransactionInfo) {
            this.oldTransactionInfo = oldTransactionInfo;
        }
    }
}
