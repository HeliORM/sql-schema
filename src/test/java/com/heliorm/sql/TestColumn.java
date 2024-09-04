package com.heliorm.sql;

import java.sql.JDBCType;
import java.util.Set;

public abstract class TestColumn  {

    private final Table table;
    private final String name;
    private final JDBCType jdbcType;
    private final boolean nullable;
    private final boolean key;
    private final boolean autoIncrement;
    private final Set<String> enumValues;
    private final String defaultValue;

    public TestColumn(Table table, String name, JDBCType jdbcType) {
        this(table, name, jdbcType, false, false, false);
    }

    public TestColumn(Table table, String name, JDBCType jdbcType, boolean nullable, boolean key, boolean autoIncrement) {
        this(table, name, jdbcType, nullable, null, key, autoIncrement, null);
    }

    public TestColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defaultValue, boolean key, boolean autoIncrement, Set<String> enumValues) {
        this.table = table;
        this.name = name;
        this.jdbcType = jdbcType;
        this.nullable  = nullable;
        this.key = key;
        this.autoIncrement = autoIncrement;
        this.enumValues =enumValues;
        this.defaultValue = defaultValue;
    }

    
    public String getName() {
        return name;
    }

    
    public JDBCType getJdbcType() {
        return jdbcType;
    }

    
    public Table getTable() {
        return table;
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

    
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (jdbcType != null ? jdbcType.hashCode() : 0);
        return result;
    }

    
    public String toString() {
        return "TestColumn{" +
                "autoIncrement=" + autoIncrement +
                ", defaultValue='" + defaultValue + '\'' +
                ", enumValues=" + enumValues +
                ", jdbcType=" + jdbcType +
                ", key=" + key +
                ", name='" + name + '\'' +
                ", nullable=" + nullable +
                ", table=" + table +
                '}';
    }
}
