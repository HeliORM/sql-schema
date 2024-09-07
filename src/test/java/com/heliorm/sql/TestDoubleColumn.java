package com.heliorm.sql;

import java.sql.JDBCType;
import java.util.Collections;

public class TestDoubleColumn extends TestColumn implements DoubleColumn{

    public TestDoubleColumn(Table table, String name) {
        super(table, name, JDBCType.DOUBLE);
    }

    public TestDoubleColumn(Table table, String name, boolean nullable, boolean key, boolean autoIncrement, String defaultValue) {
        super(table, name, JDBCType.DOUBLE, nullable,  defaultValue, key, autoIncrement, Collections.emptySet());
    }

    public TestDoubleColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defaultValue, boolean key) {
        super(table, name, jdbcType, nullable,  defaultValue, key, false, Collections.emptySet());
    }

}
