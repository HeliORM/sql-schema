package com.heliorm.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public final class SqlSteps implements En {
    private static final String DATABASE_NAME = "test";

    @Container
    public static GenericContainer mariadb = new GenericContainer(DockerImageName.parse("mariadb"))
            .withExposedPorts(3306)
            .withEnv("MYSQL_DATABASE", DATABASE_NAME)
            .withEnv("MYSQL_ROOT_PASSWORD", "dev");

    @Container
    public static GenericContainer postgres = new GenericContainer(DockerImageName.parse("postgres"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", DATABASE_NAME)
            .withEnv("POSTGRES_PASSWORD", "dev");
    private String tableName;
    private SqlModeller modeller;

    private TestTable testTable;

    public SqlSteps() {
        setupCreateSteps();
        setupCreateIndexSteps();
        setupAddColumnSteps();
        setupRenameColumnSteps();
        setupModifyColumnSteps();
    }

    private void setupCreateSteps() {
        Given("we create a table {string} in a {string} database with these columns", (String tableName, String databaseType, DataTable table) -> {
            this.tableName = tableName;
            modeller = setupModeller(databaseType);
            cleanup(modeller, tableName);
            testTable = makeTable(DATABASE_NAME, tableName, table);
            modeller.createTable(testTable);
        });
        Then("the created table must have these columns", (DataTable table) -> {
            var testTable = makeTable(DATABASE_NAME, tableName, table);
            var dbTable = modeller.readTable(modeller.readDatabase(DATABASE_NAME), tableName);
            assertThat(dbTable)
                    .as("Table read must not be null")
                    .isNotNull();
            isSameTable(dbTable, testTable, true);
        });
    }

    private void setupCreateIndexSteps() {
        Given("we then add this index {string} to the table", (String indexName, DataTable dataTable) -> {
            var index = makeIndex(testTable, indexName, dataTable);
            testTable.addIndex(index);
            modeller.addIndex(index);
        });
        Then("we the created table must have this index", (DataTable dataTable) -> {
            var dbTable = modeller.readTable(modeller.readDatabase(DATABASE_NAME), tableName);
            assertThat(dbTable)
                    .as("Table read must not be null")
                    .isNotNull();
            isSameTable(dbTable, testTable, false);
        });
    }

    private void setupAddColumnSteps() {
        Given("we add these columns to the table", (DataTable dataTable) -> {
            for (var colDef : dataTable.asMaps()) {
                modeller.addColumn(makeColumn(testTable, colDef));
            }
        });
    }

    private void setupRenameColumnSteps() {
        When("we rename these columns on the table",
                (DataTable dataTable) -> {
                    for (var colDef : dataTable.asMaps()) {
                        var from = testTable.getColumn(colDef.get("from"));
                        var to = new TestColumn(from.getTable(),
                                colDef.get("to"),
                                from.getJdbcType()) {
                        };
                        modeller.renameColumn(from, to);
                    }
                });
    }

    private void setupModifyColumnSteps() {
        Given("we modify these columns", (DataTable dataTable) -> {
            for (var colDef : dataTable.asMaps()) {
                modeller.modifyColumn(makeColumn(testTable, colDef));
            }
        });
    }

    private void cleanup(SqlModeller modeller, String tableName) throws SqlModellerException {
        var table = new TestTable(new TestDatabase(DATABASE_NAME), tableName);
        if (modeller.tableExists(table)) {
            modeller.deleteTable(table);
        }
    }

    private static TestIndex makeIndex(Table table, String name, DataTable colDefs) throws TestException {
        var index = new TestIndex(table, name, true);
        for (var colDef : colDefs.asMaps()) {
            var colName = colDef.get("column");
            var opt = table.getColumns().stream()
                    .filter(c -> c.getName().equals(colName))
                    .findAny();
            if (opt.isEmpty()) {
                throw new TestException(format("Cannot find column '%s'", colName));
            }
            index.addColumn(opt.get());
        }
        return index;
    }

    private static TestTable makeTable(String databaseName, String tableName, DataTable colDefs) throws TestException {
        var db = new TestDatabase(databaseName);
        var table = new TestTable(db, tableName);
        for (var colDef : colDefs.asMaps()) {
            table.addColumn(makeColumn(table, colDef));
        }
        return table;
    }

    private static TestColumn makeColumn(Table table, Map<String, String> colDef) throws TestException {
        var type = colDef.get("type");
        if (type == null) {
            throw new TestException("Type not defined for column");
        }
        if (type.equals("ENUM")) {
            return makeEnumColumn(table, colDef);
        }
        var name = getFromMap(colDef, "name");
        var jdbcType = JDBCType.valueOf(type);
        var nullable = asBoolean(getFromMap(colDef, "nullable", "false"));
        var isKey = asBoolean(getFromMap(colDef, "key", "false"));
        var isAutoIncrement = asBoolean(getFromMap(colDef, "autoincrement", "false"));
        return switch (jdbcType) {
            case VARCHAR, LONGVARCHAR -> new TestStringColumn(table,
                    name,
                    jdbcType,
                    nullable,
                    getFromMap(colDef, "defaultValue", null),
                    isKey,
                    isAutoIncrement,
                    asInt(getFromMap(colDef, "length", "255")));
            case INTEGER, SMALLINT -> new TestIntegerColumn(table,
                    name,
                    jdbcType,
                    nullable,
                    getFromMap(colDef, "defaultValue", null),
                    isKey);
            case DOUBLE -> new TestDecimalColumn(table, name, jdbcType);
            case DECIMAL -> {
                var length = getFromMap(colDef, "length");
                var precision = Integer.parseInt(length.split(",")[0]);
                var scale = Integer.parseInt(length.split(",")[1]);
                yield new TestDecimalColumn(table, name, precision, scale);
            }
            case BOOLEAN -> new TestBooleanColumn(table, name);
            case LONGVARBINARY -> new TestBinaryColumn(table, name, jdbcType, asInt(getFromMap(colDef, "length")));
            case DATE, TIME, TIMESTAMP -> new TestDateTimeColumn(table, name, jdbcType);
            default -> throw new TestException(format("Unsupported SQL column type '%s'", type));
        };
    }

    private static TestColumn makeEnumColumn(Table table, Map<String, String> colDef) throws TestException {
        return new TestEnumColumn(table,
                getFromMap(colDef, "name"),
                asBoolean(getFromMap(colDef, "nullable", "false")),
                Arrays.stream(getFromMap(colDef, "values").split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet()));
    }

    private static int asInt(String value) {
        return Integer.parseInt(value);
    }

    private static boolean asBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    private static String getFromMap(Map<String, String> map, String name) throws TestException {
        var val = getFromMap(map, name, null);
        if (val == null) {
            throw new TestException(format("No value for '%s'", name));
        }
        return val;
    }

    private static String getFromMap(Map<String, String> map, String name, String defaultValue) {
        if (!map.containsKey(name)) {
            return defaultValue;
        }
        var val = map.get(name);
        if (val == null) {
            return defaultValue;
        }
        if (val.isBlank()) {
            return defaultValue;
        }
        return val;
    }

    private static SqlModeller setupModeller(String dbType) throws SQLException {
        return switch (dbType) {
            case "postgresql" -> {
                var jdbcDataSource = setupPostgreSqlDatasource();
                yield SqlModeller.postgres(() -> {
                    try {
                        return jdbcDataSource.getConnection();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                });
            }
            case "mysql" -> {
                var jdbcDataSource = setupMysqlDataSource();
                yield SqlModeller.mysql(() -> {
                    try {
                        return jdbcDataSource.getConnection();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                });
            }
            default -> throw new SQLException(format("Unsupported database type '%s'", dbType));
        };
    }


    private static DataSource setupMysqlDataSource() {
        mariadb.start();
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(format("jdbc:mysql://%s:%d/%s", mariadb.getHost(), mariadb.getFirstMappedPort(), DATABASE_NAME));
        conf.setUsername("root");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }


    private static DataSource setupPostgreSqlDatasource() {
        postgres.start();
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(format("jdbc:postgresql://%s:%d/%s", postgres.getHost(), postgres.getFirstMappedPort(), DATABASE_NAME));
        conf.setUsername("postgres");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }


    private void isSameTable(Table one, TestTable other, boolean ignoreIndex) {
        assertThat(one.getDatabase().getName())
                .as("The database names must be the same (%s and %s)", one.getDatabase().getName(), other.getDatabase().getName())
                .isEqualTo(other.getDatabase().getName());
        isSameColumns(one.getColumns(), other.getColumns());
        if (!ignoreIndex) {
            isSameIndexes(one.getIndexes(), other.getIndexes());
        }
    }

    private void isSameColumns(Set<Column> one, Set<Column> other) {
        assertThat(one.size())
                .as("The size of the column sets must be the same (%d and %d)", one.size(), other.size())
                .isEqualTo(other.size());
        var oneMap = one.stream().collect(Collectors.toMap(Column::getName, col -> col));
        var otherMap = other.stream().collect(Collectors.toMap(Column::getName, col -> col));
        for (String name : oneMap.keySet()) {
            assertThat(otherMap)
                    .as("The other column set must contain %s", name)
                    .containsKey(name);
            isSameColumn(oneMap.get(name), otherMap.get(name));
        }
    }

    private void isSameIndexes(Set<Index> one, Set<Index> other) {
        assertThat(one.size())
                .as("The size of the index set must be the same (%d and %d)", one.size(), other.size())
                .isEqualTo(other.size());
        var oneMap = one.stream().collect(Collectors.toMap(Index::getName, col -> col));
        var otherMap = other.stream().collect(Collectors.toMap(Index::getName, col -> col));
        for (String name : oneMap.keySet()) {
            assertThat(otherMap)
                    .as("The other index set must contain %s", name)
                    .containsKey(name);
            isSameIndex(oneMap.get(name), otherMap.get(name));
        }
    }

    private void isSameIndex(Index one, Index other) {
        boolean same = one.getName().equals(other.getName())
                && (one.isUnique() == other.isUnique());
        assertThat(one.getName())
                .as("The index names must be the same (%s and %s)", one.getName(), other.getName())
                .isEqualTo(one.getName(), other.getName());
        isSameColumns(one.getColumns(), other.getColumns());
    }

    private void isSameColumn(Column one, Column other) {
        assertThat(one.isAutoIncrement())
                .as("Autoincrement for %s must be the same (%b and %b)", one.getName(), one.isAutoIncrement(), other.isAutoIncrement())
                .isEqualTo(other.isAutoIncrement());
        assertThat(one.isNullable())
                .as("Nullable for %s must be the same (%b and %b)", one.getName(), one.isNullable(), other.isNullable())
                .isEqualTo(other.isNullable());
        assertThat(one.isKey())
                .as("Key for %s must be the same (%b and %b)",  one.getName(), one.isKey(), other.isKey())
                .isEqualTo(other.isKey());
        assertThat(one.getName())
                .as("Name for %s must be the same (%s and %s)", one.getName(), other.getName())
                .isEqualTo(other.getName());
        assertThat(one.getDefault())
                .as("Default for %s must be the same (%s and %s)",  one.getName(), one.getDefault(), other.getDefault())
                .isEqualTo(other.getDefault());
        assertThat(modeller.typesAreCompatible(one, other))
                .as("The types for %s must be compatible (%s vs %s)",  one.getName(), one.getJdbcType(), other
                        .getJdbcType())
                .isTrue();
    }

}
