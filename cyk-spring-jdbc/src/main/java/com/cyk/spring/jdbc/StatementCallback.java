/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * The interface StatementCallback
 *
 * @author yukang.chen
 * @date 2025/5/29
 */
@FunctionalInterface
public interface StatementCallback<T> {

    T doInStatement(Statement statement) throws SQLException;
}
