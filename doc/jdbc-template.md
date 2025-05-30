JdbcTemplate 是对底层 jdbc 操作的封装，用模板方法模式封装了打开连接、关闭连接、执行 sql、异常转译等功能。
RowMapper 是对数据库查询结果集映射成 pojo 类的顶层接口，可以分为基本数据类型映射和具体 pojo 类型映射
为了方便测试，使用 Sqlite 数据库和 HikariCP 作为数据库连接池