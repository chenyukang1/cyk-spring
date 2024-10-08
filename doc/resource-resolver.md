在编写IOC容器之前，我们首先要实现@ComponentScan，即解决“在指定包下扫描所有Class”的问题。

Java的ClassLoader机制可以在指定的Classpath中根据类名加载指定的Class，但遗憾的是，给出一个包名，例如，org.example，
它并不能获取到该包下的所有Class，也不能获取子包。要在Classpath中扫描指定包名下的所有Class，包括子包，
实际上是在Classpath中搜索所有文件，找出文件名匹配的.class文件。 例如，Classpath中搜索的文件org/example/Hello.class
就符合包名org.example，我们需要根据文件路径把它变为org.example.Hello，就相当于获得了类名。因此，搜索Class变成了搜索文件。

几个要点：

- ClassLoader首先从Thread.getContextClassLoader()获取，如果获取不到，再从当前Class获取。
因为Web应用的ClassLoader不是JVM提供的基于Classpath的ClassLoader，而是Servlet容器提供的ClassLoader，
它不在默认的Classpath搜索，而是在/WEB-INF/classes目录和/WEB-INF/lib的所有jar包搜索，
从Thread.getContextClassLoader()可以获取到Servlet容器专属的ClassLoader

- Windows和Linux/macOS的路径分隔符不同，前者是\，后者是/，需要正确处理

- 扫描目录时，返回的路径可能是abc/xyz，也可能是abc/xyz/，需要注意处理末尾的/