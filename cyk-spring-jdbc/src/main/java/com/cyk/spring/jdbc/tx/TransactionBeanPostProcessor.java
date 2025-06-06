package com.cyk.spring.jdbc.tx;

import com.cyk.spring.aop.AnnotationProxyBeanPostProcessor;
import com.cyk.spring.jdbc.tx.annotation.Transactional;

/**
 * The class TransactionBeanPostProcessor
 *
 * @author yukang.chen
 * @date 2025/6/4
 */
public class TransactionBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Transactional> {
}
