package com.heliorm.sql;

import java.sql.JDBCType;

final class SqlDateColumn extends SqlColumn implements DateColumn {

    SqlDateColumn(Table table, String name, boolean nullable, String defVal) {
        super(table, name, JDBCType.DATE, nullable, defVal, false);
    }
}
