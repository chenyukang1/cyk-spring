spring 事务支持多种传播机制，默认是 REQUIRED，即如果当前存在事务，则加入该事务，如果当前没有事务，则创建一个新的事务。
这里利用 threadLocal 只实现了 REQUIRED 级别，且只支持 JDBC 本地事务。在实习上，完全借鉴了 spring-tx 的类设计，提取
了核心实现类的主要骨架，定义了 TransactionStatus、TransactionDefinition、TransactionObject 多种抽象。

核心原理：利用之前实现的 aop 框架，定义出事务的 InvocationHandler，注册一个 BeanPostProcessor 扫描 bean 上所有标注了
 @Transactional 的方法，由 ByteBuddy 生成动态代理类，代理对象的方法指向 InvocationHandler，最终在 TransactionInvocationHandler
 中完成所有事务提交/回滚等等