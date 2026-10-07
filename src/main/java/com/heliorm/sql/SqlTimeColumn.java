package com.heliorm.sql;

import java.sql.JDBCType;

final class SqlTimeColumn extends SqlColumn implements TimeColumn {

    SqlTimeColumn(Table table, String name, boolean nullable, String defVal) {
        super(table, name, JDBCType.TIME, nullable, defVal, false);
    }
}
