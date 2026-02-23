package com.heliorm.sql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Implementation of Index that is populated by reading from SQL
 *
 */
final class SqlIndex implements Index {

    private final Table table;
    private final String name;
    private final boolean unique;
    private final Map<String, Column> columns;

    public SqlIndex(Table table, String name, boolean unique) {
        this.table = table;
        this.name = name;
        this.unique = unique;
        this.columns = new HashMap<>();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Table table() {
        return table;
    }

    @Override
    public Set<Column> columns() {
        return new HashSet<>(columns.values());
    }

    @Override
    public boolean unique() {
        return unique;
    }

    void addColunm(Column column) {
        columns.put(column.name(), column);
    }
}
