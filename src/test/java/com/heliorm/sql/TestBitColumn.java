package com.heliorm.sql;

import java.sql.JDBCType;

public final class TestBitColumn extends TestColumn implements BitColumn{

    private final int bits;

    public TestBitColumn(Table table, String name, int bits) {
        super(table, name, JDBCType.BIT);
        this.bits = bits;
    }

    @Override
    public int getBits() {
        return bits;
    }
}
