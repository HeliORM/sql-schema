package com.heliorm.sql;

import java.sql.JDBCType;


/** Implementation of string column that is populated by reading from SQL */
final class SqlStringColumn extends SqlColumn implements StringColumn {

    private final int length;

    public SqlStringColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defVal, int length) {
        super(table, name, jdbcType, nullable, defVal, false);
        this.length = length;
    }

    @Override
    public int getLength() {
        return length;
    }
}
