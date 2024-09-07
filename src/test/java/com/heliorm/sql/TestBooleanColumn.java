package com.heliorm.sql;

import java.sql.JDBCType;
import java.util.List;
import java.util.Set;

public final class TestBooleanColumn extends TestColumn implements BooleanColumn {
    public TestBooleanColumn(Table table, String name, String def) {
        super(table, name, JDBCType.BOOLEAN, false, def, false, false, Set.of());
    }
}
