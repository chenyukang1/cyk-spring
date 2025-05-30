/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.jdbc.mapper;

import com.cyk.spring.jdbc.RowMapper;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The class BasicDataTypeMapper
 *
 * @author yukang.chen
 * @date 2025/5/30
 */
public class BasicDataTypeMapper<T> implements RowMapper<T> {

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return (T) rs.getObject(1);
    }

}
