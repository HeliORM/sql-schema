package com.heliorm.sql;

import java.util.HashSet;
import java.util.Set;

public final class TestDatabase implements Database {

    private final String name;
    private final Set<Table> tables;

    public TestDatabase(String name) {
        this.name = name;
        this.tables = new HashSet<>();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Set<Table> tables() {
        return tables;
    }
}
