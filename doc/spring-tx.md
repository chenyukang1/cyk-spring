spring 事务支持多种传播机制，默认是 REQUIRED，即如果当前存在事务，则加入该事务，如果当前没有事务，则创建一个新的事务。
这里利用 threadLocal 只实现了 REQUIRED 级别，且只支持 JDBC 本地事务。在实习上，完全借鉴了 spring-tx 的类设计，提取
了核心实现类的主要骨架，定义了 TransactionStatus、TransactionDefinition、TransactionObject 多种抽象。

核心原理：利用之前实现的 aop 框架，定义出事务的 InvocationHandler，注册一个 BeanPostProcessor 扫描 bean 上所有标注了
 @Transactional 的方法，由 ByteBuddy 生成动态代理类，代理对象的方法指向 InvocationHandler，最终在 TransactionInvocationHandler
 中完成所有事务提交/回滚等等

几个细节：
- setter 注入时要注入代理 bean，否则如果注入原始 bean 不会被代理，事务不起作用
- ThreadLocal 保证当前线程是否绑定了事务，事务中的 ThreadLocal 保证当前线程是否绑定了数据库连接，最终保证 REQUIRED 级别能
加入当前事务，复用同一个数据库连接提交或回滚所有的 sql