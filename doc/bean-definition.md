本章的实现可以直接参考 spring-context，Spring 的 IOC 容器分为两类：BeanFactory 和 ApplicationContext，
前者总是延迟创建 Bean，而后者则在启动时初始化所有 Bean。实际使用时，99% 都采用 ApplicationContext，这里我们只实现后者

每一个受管的对象，在容器中都会有一个 BeanDefinition 的实例(instance)与之相对应，
该 BeanDefinition 的实例负责保存对象的所有必要信息，包括其对应的对象的 class 类型、是否是抽象类、构造方法参数以及其他属性等

BeanDefinition 的装配方式有两种

- 从当前包开始扫描，装配所有注解了 @Component 的类
- 从当前包开始扫描，装配所有注解了 @Configuration 的类下注解了 @Bean 方法返回的实例

具体的实现中通过组合的方式，AnnotationConfigApplicationContext 把加载 BeanDefinition 的工作委托给 IBeanDefinitionHandler
IBeanDefinitionHandler 组合了 IBeanDefinitionAssemble，负责扫描和装配 BeanDefinition
IBeanDefinitionAssemble 负责具体的反射方式做 BeanDefinition 转换