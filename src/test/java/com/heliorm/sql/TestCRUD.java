package com.heliorm.sql;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.JDBCType;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation.class)
public final class TestCRUD extends AbstractSqlTest {

    @Test
    @Order(1)
    public void createTable() throws SqlModellerException {
        modeller.createTable(table);
        assertTrue(modeller.tableExists(table), "Table must exist after creation");
    }

    @Test
    @Order(20)
    public void addDateTimeColumn() throws SqlModellerException {
        var created = new TestDateTimeColumn(table, "created", JDBCType.TIMESTAMP, false);
        table.addColumn(created);
        modeller.addColumn(created);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(21)
    public void addBinaryColumn() throws SqlModellerException {
        var content = new TestBinaryColumn(table, "content", JDBCType.LONGVARBINARY, 1024*1024);
        table.addColumn(content);
        modeller.addColumn(content);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(22)
    public void addBitColumn() throws SqlModellerException {
        var bits = new TestBitColumn(table, "bits", 8);
        table.addColumn(bits);
        modeller.addColumn(bits);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(31)
    public void modifyDecimalColumn() throws SqlModellerException {
        var amount = new TestDecimalColumn(table, "amount", 18, 5);
        table.addColumn(amount);
        modeller.modifyColumn(amount);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(33)
    public void changeStringColumnDefault() throws SqlModellerException {
        var surname = new TestStringColumn(table, "surname", JDBCType.LONGVARCHAR, false, "Smith", false, false, 30);
        table.addColumn(surname);
        modeller.modifyColumn(surname);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(34)
    public void addIntegerColumnWithDefault() throws SqlModellerException {
        var len = new TestIntegerColumn(table, "length", JDBCType.BIGINT, false, "0", false);
        table.addColumn(len);
        modeller.addColumn(len);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(40)
    public void addEnumColumn() throws SqlModellerException {
        var type = new TestEnumColumn(table, "type", true, new HashSet<>(Arrays.asList("APE", "BEAST")));
        table.addColumn(type);
        modeller.addColumn(type);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(50)
    public void addEnumValue() throws SqlModellerException {
        var type = new TestEnumColumn(table, "type", true, new HashSet<>(Arrays.asList("APE", "BEAST", "COW")));
        table.addColumn(type);
        modeller.modifyColumn(type);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(60)
    public void removeEnumValue() throws SqlModellerException {
        var type = new TestEnumColumn(table, "type", true, new HashSet<>(Arrays.asList("APE", "COW")));
        table.addColumn(type);
        modeller.modifyColumn(type);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }


    @Test
    @Order(61)
    public void addSetColumn() throws SqlModellerException {
        var col = new TestSetColumn(table, "selection", true, new HashSet<>(Arrays.asList("BREAKFAST", "LUNCH", "DINNER")));
        if (modeller.supportsSet()) {
            table.addColumn(col);
            modeller.addColumn(col);
            var loaded = modeller.readTable(db, "Person");
            assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
        } else {
            assertThrows(SqlModellerException.class, () -> modeller.addColumn(col), "Adding set column must fail");
        }
    }

    @Test
    @Order(62)
    public void addSetValue() throws SqlModellerException {
        var col = new TestSetColumn(table, "selection", true, new HashSet<>(Arrays.asList("BREAKFAST", "2ND BREAKFAST", "LUNCH", "DINNER")));
        if (modeller.supportsSet()) {
            table.addColumn(col);
            modeller.modifyColumn(col);
            var loaded = modeller.readTable(db, "Person");
            assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
        } else {
            assertThrows(SqlModellerException.class, () -> modeller.modifyColumn(col), "Modifying set column must fail");
        }
    }

    @Test
    @Order(63)
    public void removeSetValue() throws SqlModellerException {
        var col = new TestEnumColumn(table, "selection", true, new HashSet<>(Arrays.asList("BREAKFAST", "LUNCH", "DINNER")));
        if (modeller.supportsSet()) {
            table.addColumn(col);
            modeller.modifyColumn(col);
            var loaded = modeller.readTable(db, "Person");
            assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
        } else {
            assertThrows(SqlModellerException.class, () -> modeller.modifyColumn(col), "Modifying set column must fail");
        }
    }

    @Test
    @Order(70)
    public void deleteColumn() throws SqlModellerException {
        var email = new TestStringColumn(table, "email", JDBCType.VARCHAR, 128);
        var notes = new TestStringColumn(table, "notes", JDBCType.LONGVARCHAR, 1000);
        table.deleteColumn(email);
        table.deleteColumn(notes);
        modeller.deleteColumn(email);
        modeller.deleteColumn(notes);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(80)
    public void modifyColumnLength() throws SqlModellerException {
        var name = new TestStringColumn(table, "fullName", JDBCType.VARCHAR, true, false, 64);
        table.addColumn(name);
        modeller.modifyColumn(name);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(90)
    public void modifyColumnTypeSmallIntBigInt() throws SqlModellerException {
        var age = new TestIntegerColumn(table, "age", JDBCType.BIGINT, false, false, false);
        table.addColumn(age);
        var loaded = modeller.readTable(db, "Person");
        assertFalse(isSameTable(loaded, table), "Table we modified must not be the same as the one loaded");
        modeller.modifyColumn(age);
        loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(100)
    public void modifyColumnType() throws SqlModellerException {
        var contact = new TestBooleanColumn(table, "contact");
        table.addColumn(contact);
        modeller.modifyColumn(contact);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(110)
    public void addSingleColumnIndex() throws SqlModellerException {
        var index = new TestIndex(table, "index0", true);
        index.addColumn(table.getColumn("fullName"));
        table.addIndex(index);
        modeller.addIndex(index);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }


    @Test
    @Order(111)
    public void addMultiColumnIndex() throws SqlModellerException {
        var index = new TestIndex(table, "index1", true);
        index.addColumn(table.getColumn("fullName"));
        index.addColumn(table.getColumn("age"));
        table.addIndex(index);
        modeller.addIndex(index);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(112)
    public void changeIndexUninqueness() throws SqlModellerException {
        var index = new TestIndex(table, "index1", false);
        index.addColumn(table.getColumn("fullName"));
        index.addColumn(table.getColumn("age"));
        table.addIndex(index);
        modeller.modifyIndex(index);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }


    @Test
    @Order(120)
    public void addColumnToIndex() throws SqlModellerException {
        var index = (TestIndex) table.getIndex("index0");
        index.addColumn(table.getColumn("age"));
        table.addIndex(index);
        modeller.modifyIndex(index);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(121)
    public void renameIndex() throws SqlModellerException {
        var index = (TestIndex) table.getIndex("index0");
        var index1 = new TestIndex(table, "index7", index.unique());
        for (Column column : index.columns()) {
            index1.addColumn(column);
        }
        modeller.renameIndex(index, index1);
        table.removeIndex(index);
        table.addIndex(index1);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(130)
    public void removeIndex() throws SqlModellerException {
        Index index = table.getIndex("index1");
        table.removeIndex(index);
        modeller.removeIndex(index);
        var loaded = modeller.readTable(db, "Person");
        assertTrue(isSameTable(loaded, table), "Table we modified must be the same as the one loaded");
    }

    @Test
    @Order(140)
    public void deleteTable() throws SqlModellerException {
        modeller.deleteTable(table);
        assertFalse(modeller.tableExists(table), "Table must not exist any more");
    }

}
