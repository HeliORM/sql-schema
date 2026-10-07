package com.heliorm.sql;

import java.sql.JDBCType;

public final class TestTimeColumn extends TestColumn implements TimeColumn {

    public TestTimeColumn(Table table, String name) {
        super(table, name, JDBCType.TIME);
    }

    public TestTimeColumn(Table table, String name, boolean nullable) {
        super(table, name, JDBCType.TIME, nullable, false, false);
    }

}
