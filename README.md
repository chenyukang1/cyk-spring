# 手写SpringBoot

## 写在前面

参考自廖大的博客：https://liaoxuefeng.com/books/summerframework/introduction/index.html

学习一个框架最好的方法是实现一个

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