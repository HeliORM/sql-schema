package com.heliorm.sql;

import java.sql.JDBCType;

/**
 * Implementation of decimal column that is populated by reading from SQL
 */
final class SqlDecimalColumn extends SqlColumn implements DecimalColumn {

    private int precision;
    private int scale;

    public SqlDecimalColumn(Table table, String name, JDBCType jdbcType, boolean nullable, String defVal, int precision, int scale) {
        super(table, name, jdbcType, nullable, defVal, false);
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int getScale() {
        return scale;
    }
}
