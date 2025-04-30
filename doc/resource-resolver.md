在编写 IOC 容器之前，我们首先要实现 @ComponentScan，即解决“在指定包下扫描所有 Class”的问题

Java 的 ClassLoader 机制可以在指定的 Classpath 中根据类名加载指定的 Class，但遗憾的是，给出一个包名，例如，org.example，
它并不能获取到该包下的所有 Class，也不能获取子包。要在 Classpath 中扫描指定包名下的所有 Class，包括子包，
实际上是在 Classpath 中搜索所有文件，找出文件名匹配的.class文件。 例如，Classpath 中搜索的文件 org/example/Hello.class
就符合包名 org.example，我们需要根据文件路径把它变为 org.example.Hello，就相当于获得了类名。因此，搜索 Class 变成了搜索文件

几个要点：

- ClassLoader 首先从 Thread.getContextClassLoader() 获取，如果获取不到，再从当前 Class 获取。
因为 Web 应用的 ClassLoader 不是 JVM 提供的基于 Classpath 的 ClassLoader，而是 Servlet 容器提供的 ClassLoader，
它不在默认的 Classpath 搜索，而是在 /WEB-INF/classes 目录和 /WEB-INF/lib 的所有 jar 包搜索，
从 Thread.getContextClassLoader() 可以获取到 Servlet 容器专属的 ClassLoader

- Windows 和 Linux/macOS 的路径分隔符不同，前者是\，后者是/，需要正确处理

- 扫描目录时，返回的路径可能是 abc/xyz，也可能是 abc/xyz/，需要注意处理末尾的/