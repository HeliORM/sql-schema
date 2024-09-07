package com.heliorm.sql;

import java.sql.JDBCType;

public final class TestBooleanColumn extends TestColumn implements BooleanColumn {
    public TestBooleanColumn(Table table, String name) {
        super(table, name, JDBCType.BOOLEAN);
    }
}
