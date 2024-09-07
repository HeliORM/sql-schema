package com.heliorm.sql;

import java.sql.JDBCType;

public final class TestDateTimeColumn extends TestColumn implements DateTimeColumn{


    public TestDateTimeColumn(Table table, String name, JDBCType jdbcType) {
        super(table, name, jdbcType);
    }

    public TestDateTimeColumn(Table table, String name, JDBCType jdbcType, boolean nullable) {
        super(table, name, jdbcType, nullable, false,false);
    }

}
