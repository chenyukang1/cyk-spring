AOP 是一种编程范式，它通过将横切关注点(cross-cutting concerns)从主业务逻辑中分离出来，来提高代码的模块化程度。 简单来说，它允许我们在不修改原有代码的情况下，给程序动态添加一些功能
Spring AOP 提供了一套注解来实现 AOP，并且在日常业务开发中也有广泛的应用，如：
- 日志记录
- 性能监控
- 横向鉴权

如何实现 AOP 呢？在我们使用 Spring AOP 的时候，需要定义切点，然后通过 @Around、@Before、@After、@AfterReturning、@AfterThrowing 等注解来定义切面。
被代理的类需要生成一个代理类，代理类执行切面的方法，然后执行被代理类的原方法，JDK 动态代理可以实现对接口的代理，而对类的代理需要引入三方库，如
CGLIB、ByteBuddy 等，CGLIB 目前不再维护，所以使用 ByteBuddy

@Around 注解可以由 BeanPostProcessor + InvocationHandler 实现，只需内置一个 BeanPostProcessor 扫描标注了 @Around 的 bean，
然后动态生成代理类，调用代理类的方法时通过 InvocationHandler 调用被代理的原始方法