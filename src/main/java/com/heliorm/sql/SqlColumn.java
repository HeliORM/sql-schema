package com.heliorm.sql;

import java.sql.JDBCType;


/** Implementation of a column that is populated by reading from SQL
 *
 */
abstract class SqlColumn  {

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

    
    public Table getTable() {
        return table;
    }

    
    public String getName() {
        return name;
    }

    
    public JDBCType getJdbcType() {
        return jdbcType;
    }

    
    public boolean isNullable() {
        return nullable;
    }

    
    public boolean isKey() {
        return key;
    }

    
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    
    public String getDefault() {
        return defaultValue;
    }

    void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    
    
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
