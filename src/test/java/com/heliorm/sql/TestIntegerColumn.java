package com.heliorm.sql;

import java.sql.JDBCType;
import java.util.Collections;

public class TestIntegerColumn extends TestColumn implements IntegerColumn{

    public TestIntegerColumn(Table table, String name) {
        super(table, name, JDBCType.INTEGER);
    }

    public TestIntegerColumn(Table table, String name, boolean nullable, boolean key, boolean autoIncrement, String defaultValue) {
        super(table, name, JDBCType.INTEGER, nullable,  defaultValue, key, autoIncrement, Collections.emptySet());
    }

    public TestIntegerColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defaultValue, boolean key) {
        super(table, name, jdbcType, nullable,  defaultValue, key, false, Collections.emptySet());
    }

}
