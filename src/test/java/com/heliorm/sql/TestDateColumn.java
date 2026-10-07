package com.heliorm.sql;

import java.sql.JDBCType;

public final class TestDateColumn extends TestColumn implements DateColumn {

    public TestDateColumn(Table table, String name) {
        super(table, name, JDBCType.DATE);
    }

    public TestDateColumn(Table table, String name, boolean nullable) {
        super(table, name, JDBCType.DATE, nullable, false, false);
    }

}
