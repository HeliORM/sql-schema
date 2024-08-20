package com.heliorm.sql;

import java.sql.JDBCType;

final class SqlBinaryColumn extends SqlColumn implements BinaryColumn {

    private final int length;

    SqlBinaryColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defVal, int length) {
        super(table, name, jdbcType, nullable, defVal, false);
        this.length = length;
    }

    @Override
    public int getLength() {
        return length;
    }
}