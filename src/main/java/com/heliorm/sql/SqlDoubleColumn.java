package com.heliorm.sql;

import java.sql.JDBCType;

public final class SqlDoubleColumn extends SqlColumn implements DoubleColumn {

    SqlDoubleColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defaultValue) {
        super(table, name, jdbcType, nullable, defaultValue, false);
    }
}
