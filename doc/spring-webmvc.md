Java 提供了 Servlet API 来处理 HTTP 请求和响应，而各厂商比如 Jetty, Tomcat 实现了相关 API。Spring Web MVC 是一个基于 Servlet API 的框架，
它提供了更高级的抽象来简化 Web 应用程序的开发

Servlet 规范定义的组件有 3 类：
- Servlet：处理 HTTP 请求，然后输出响应
- Filter：对 HTTP 请求进行过滤，可以有多个 Filter 形成过滤器链，实现权限检查、限流、缓存等逻辑
- Listener：用来监听 Web 应用程序产生的事件，包括启动、停止、Session 修改等

具体来说，在 Servlet 容器初始化后
1. Listener 监听到初始化，加载配置，创建 IOC 容器
2. 创建 DispatcherServlet 实例，并向 Servlet 容器注册
3. DispatcherServlet 获取 IOC 容器中的 Controller，接收到 HTTP 请求时调用 HandlerMapping 查找对应的 Controller

这三块功能实现后，把 cyk-spring-web 模块打成 war 包，放到 Tomcat 的 webapps 目录下，启动 Tomcat，可以看到 IOC 容器成功加载，application.yml
中的配置被成功读出，基本的架子就完成了