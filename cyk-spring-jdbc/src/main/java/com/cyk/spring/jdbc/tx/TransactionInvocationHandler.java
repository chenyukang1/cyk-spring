package com.cyk.spring.jdbc.tx;

import com.cyk.spring.jdbc.exception.TransactionException;
import com.cyk.spring.jdbc.tx.annotation.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        Transactional transactional = method.getAnnotatedReturnType().getAnnotation(Transactional.class);
        TransactionDefinition definition = determinTransactionDefinition(transactional);
        TransactionInfo txInfo = createTransactionIfNecessary(ptm, definition);
        return null;
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
