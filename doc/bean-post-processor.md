BeanPostProcessor 是 Spring Framework 中最核心的扩展机制之一，它允许你在 Spring 容器初始化 Bean 的过程中进行自定义操作，比如：
- 修改 Bean 的属性
- 包装成代理对象（如 AOP）
- 注入额外逻辑
- 做字段/注解扫描处理

假设通过 BeanPostProcessor 实现了 UserService 代理类 UserServiceProxy，来通过事务增强 UserService的功能

从业务逻辑出发
- JdbcTemplate 实例必须注入到原始的 UserService，否则，代理类 UserServiceProxy 执行 target.register() 
时，相当于对原始的 UserService 调用 register() 方法，如果 JdbcTemplate 没有注入，将直接报 NullPointerException 错误
- MvcController 需要注入的 UserService 必须是 UserServiceProxy，否则事务不起作用
  ┌───────────────┐
  │MvcController  │
  ├───────────────┤   ┌────────────────┐
  │- userService ─┼──▶│UserServiceProxy│
  └───────────────┘   ├────────────────┤
                      │- jdbcTemplate  │
                      ├────────────────┤   ┌────────────────┐
                      │- target       ─┼──▶│UserService     │
                      └────────────────┘   ├────────────────┤   ┌────────────┐
                                           │- jdbcTemplate ─┼──▶│JdbcTemplate│
                                           └────────────────┘   └────────────┘

