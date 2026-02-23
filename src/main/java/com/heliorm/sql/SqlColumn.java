package com.heliorm.sql;

import java.sql.JDBCType;


/** Implementation of a column that is populated by reading from SQL
 *
 */
abstract non-sealed class SqlColumn implements Column {

    private final Table table;
    private final String name;
    private final JDBCType jdbcType;
    private boolean nullable;
    private boolean key;
    private final boolean autoIncrement;
    private final String defaultValue;

    SqlColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defaultValue, boolean autoIncrement) {
        this.table = table;
        this.name = name;
        this.jdbcType = jdbcType;
        this.nullable = nullable;
        this.key = false;
        this.autoIncrement = autoIncrement;
        this.defaultValue = defaultValue;
    }

    void setKey(boolean key) {
        this.key = key;
    }

    @Override
    public Table table() {
        return table;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public JDBCType jdbcType() {
        return jdbcType;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public boolean key() {
        return key;
    }

    @Override
    public boolean autoIncrement() {
        return autoIncrement;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    
    @Override
    public String toString() {
        return "SqlColumn{" +
                "autoIncrement=" + autoIncrement +
                ", defaultValue='" + defaultValue + '\'' +
                ", jdbcType=" + jdbcType +
                ", key=" + key +
                ", name='" + name + '\'' +
                ", nullable=" + nullable +
                ", table=" + table +
                '}';
    }
}
