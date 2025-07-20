# 手写SpringBoot

## 为什么要动手实现

学习一个框架最好的方法是实现一个，从技术成长的角度看，springboot 框架对 java 程序员的重要性相当之大，支撑起了整个微服务生态，因此理解它的底
层设计细节很重要

第二个问题是想要看懂 springboot 的源码也不是一件简单的事情，如果你从 SpringApplication#run 开始 debug，配合许多的源码解读资料搞懂全部流
程要耗费大量精力，我认为首先搞懂设计再深入细节是更好的方式，比如先看一本《Spring揭秘》
其次源码的复杂度和抽象性很高，也没必要面面俱到弄懂所有细节，相反它的核心链路代码写出来可能就几w行，但是考虑扩展性、代码优雅整洁、设计模式等等，
源码又会做相当多的包装，很容易迷失其中，比如 ApplicationContext 庞大的继承树

因此这个工程的价值就是从 springboot 设计理念出发，简单的还原其技术细节，减少了很多学习成本，至少比硬啃源码高效的多

## 实现思路

| ID  | Problem             | Article                             | 
|-----|---------------------|:------------------------------------|
| 000 | 实现资源扫描器             | [解决思路](/doc/resource-resolver.md)   |
| 001 | 创建BeanDefinition    | [解决思路](/doc/bean-definition.md)     |
| 002 | 创建Bean实例            | [解决思路](/doc/bean-instance.md)       |
| 003 | 实现BeanPostProcessor | [解决思路](/doc/bean-post-processor.md) |
| 004 | 实现AOP               | [解决思路](/doc/spring-aop.md)          |
| 005 | 实现JDBCTemplate      | [解决思路](/doc/jdbc-template.md)       |
| 006 | 实现事务                | [解决思路](/doc/spring-tx.md)           |
| 007 | 实现WebMvc            | [解决思路](/doc/spring-web-mvc.md)      |
| 007 | 实现Boot              | [解决思路](/doc/spring-boot.md)         |

## 总结

总共用了 7k 多行代码实现了一个迷你的 springboot，不得不说“绝知此事要躬行”。如果说还有哪些东西值得实现的话，其实还有很多，
spring 框架提供了相当强的扩展性，比如：
- 实现 FactoryBean，FactoryBean 是 spring 提供的工厂 bean，用于复杂 Bean 的创建（如代理对象、连接池等）
- 实现 spring 的 spi 机制
- 实现事务的其他常用传播机制，nested/new
- springboot 是通过 JarLauncher，使用自定义的 ClassLoader 去加载 class 和 jar 包，而且提供了 Maven 插件，自动设置 Main-Class。
  这部分实现起来比较复杂，而本项目是通过解压 war 包的办法实现 Tomcat 的 ClassLoader 加载