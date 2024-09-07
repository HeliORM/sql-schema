package com.heliorm.sql;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.heliorm.sql.diffs.Compare.compare;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class AddColumnSteps extends AbstractSqlTest implements En {

    private String tableName;
    private Database database;
    private Table table;
    private SqlModeller modeller;

    public AddColumnSteps() {
        Before(this::setup);
        database = new TestDatabase("test");
        modeller = SqlModeller.mysql(this::con);
        Given("A table has these columns", (DataTable dataTable) -> {
            tableName = UUID.randomUUID().toString().substring(0, 8);
            createTable(tableName, dataTable.asMaps());
        });
        When("columns are added", (io.cucumber.datatable.DataTable dataTable) -> {
            addColumns(tableName, dataTable.asMaps());
        });
        Then("the table must have these columns", (DataTable dataTable) -> {
            verifyColumns(tableName, dataTable.asMaps());
        });


    }


    private void createTable(String name, List<Map<String, String>> columns) throws SqlModellerException, TestException {
        table = makeTable(name, columns);
        modeller.createTable(table);
    }

    private void addColumns(String tableName, List<Map<String, String>> maps) throws SqlModellerException {
        var columns = maps.stream()
                .map(map -> makeColumn(table, map)).toList();
        for (var column : columns) {
            modeller.addColumn(column);
        }
    }

    private void verifyColumns(String tableName, List<Map<String, String>> maps) throws SqlModellerException {
        var have = modeller.readTable(database, tableName);
        var want = makeTable(tableName, maps);
        var diffs = compare(have, want);
        assertThat(diffs)
                .as("There should be no differences between tables, but there are %d", diffs.size())
                .isEmpty();
    }

    private Table makeTable(String tableName, List<Map<String, String>> maps) throws TestException {
        var table = new TestTable(database, tableName);
        for (var map : maps) {
            table.addColumn(makeColumn(table, map));
        }
        return table;
    }

    private Column makeColumn(Table table, Map<String, String> map) throws TestException {
        var name = map.get("name");
        var type = map.get("type");
        var nullable = Boolean.valueOf(map.get("nullable"));
        var autoInc = Boolean.valueOf("autoincrement");
        var key = Boolean.valueOf("key");
        var def = map.getOrDefault("default", "");
        return switch (type) {
            case "integer" -> new TestIntegerColumn(table, name, nullable, key, autoInc, def.isEmpty() ? null : def);
            case "double" -> new TestDoubleColumn(table, name, nullable, key, autoInc, def.isEmpty() ? null : def);
            case "boolean" -> new TestBooleanColumn(table, name, def.isEmpty() ? null : def);
            default -> {
                if (type.startsWith("varchar(")) {
                    var len = Integer.parseInt(type.split("\\(")[1].split("\\)")[0]);
                    yield new TestStringColumn(table, name, nullable, key, autoInc, len, def);
                }
                throw new TestException(format("Don't know SQL type '%s'", type));
            }
        };
    }

}
