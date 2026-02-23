package com.heliorm.sql;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

class AbstractSqlTest {
    private static final String DB = "test";
    private static DataSource jdbcDataSource;
    protected static SqlModeller modeller;
    protected static SqlVerifier verifier;
    protected static TestDatabase db = new TestDatabase(DB);
    protected static TestTable table;

    static {
        table = new TestTable(db, "Person");
        table.addColumn(new TestIntegerColumn(table, "id", JDBCType.INTEGER, false, true, true));
        table.addColumn(new TestDecimalColumn(table, "amount", JDBCType.DECIMAL));
        table.addColumn(new TestStringColumn(table, "surname", JDBCType.VARCHAR, 255));
        table.addColumn(new TestStringColumn(table, "email", JDBCType.VARCHAR, 128));
        table.addColumn(new TestStringColumn(table, "notes", JDBCType.LONGVARCHAR, 65535));
        table.addColumn(new TestStringColumn(table, "fullName", JDBCType.VARCHAR, 255));
        table.addColumn(new TestIntegerColumn(table, "age", JDBCType.SMALLINT));
        table.addColumn(new TestIntegerColumn(table, "contact", JDBCType.SMALLINT));
        db.tables().add(table);
    }

    @Container
    public static GenericContainer mariadb = new GenericContainer(DockerImageName.parse("mariadb"))
            .withExposedPorts(3306)
            .withEnv("MYSQL_DATABASE", DB)
            .withEnv("MYSQL_ROOT_PASSWORD", "dev");

    @Container
    public static GenericContainer postgres = new GenericContainer(DockerImageName.parse("postgres"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", DB)
            .withEnv("POSTGRES_PASSWORD", "dev");


    @BeforeAll
    public static void setup() {
        String dbType = System.getenv("TEST_DB");
        dbType = (dbType == null) ? "mysql" : dbType;
        switch (dbType) {
            case "postgresql":
                jdbcDataSource = setupPostgreSqlDatasource();
                modeller = SqlModeller.postgres(() -> {
                    try {
                        return jdbcDataSource.getConnection();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                });
                break;
            case "mysql":
            default:
                jdbcDataSource = setupMysqlDataSource();
                modeller = SqlModeller.mysql(() -> {
                    try {
                        return jdbcDataSource.getConnection();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                });
                break;

        }
        say("Using %s data source", dbType);
        verifier = SqlVerifier.forModeller(modeller, true, false);
    }

    protected static void say(String fmt, Object... args) {
        System.out.printf(fmt, args);
        System.out.println();
    }

    private static DataSource setupMysqlDataSource() {
        mariadb.start();
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(format("jdbc:mysql://%s:%d/" + DB, mariadb.getHost(), mariadb.getFirstMappedPort()));
        conf.setUsername("root");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }


    private static DataSource setupPostgreSqlDatasource() {
        postgres.start();
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(format("jdbc:postgresql://%s:%d/" + DB, postgres.getHost(), postgres.getFirstMappedPort()));
        conf.setUsername("postgres");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }

    protected boolean isSameTable(Table one, TestTable other) {
        return one.getDatabase().name().equals(other.getDatabase().name()) && isSameColumns(one.getColumns(), other.getColumns()) && isSameIndexes(one.getIndexes(), other.getIndexes());
    }

    protected boolean isSameColumns(Set<Column> one, Set<Column> other) {
        if (one.size() != other.size()) {
            say("Columns: one.size %d != other.size %d", one.size(), other.size());
            return false;
        }
        Map<String, Column> oneMap = one.stream().collect(Collectors.toMap(Column::name, col -> col));
        Map<String, Column> otherMap = other.stream().collect(Collectors.toMap(Column::name, col -> col));
        for (String name : oneMap.keySet()) {
            if (!otherMap.containsKey(name)) {
                say("other doesn't have %s", name);
                return false;
            }
            if (!isSameColumn(oneMap.get(name), otherMap.get(name))) {
                return false;
            }
        }
        return true;
    }

    protected boolean isSameIndexes(Set<Index> one, Set<Index> other) {
        if (one.size() != other.size()) {
            say("Indexes: one.size %d != other.size %d", one.size(), other.size());
            return false;
        }
        Map<String, Index> oneMap = one.stream().collect(Collectors.toMap(Index::name, col -> col));
        Map<String, Index> otherMap = other.stream().collect(Collectors.toMap(Index::name, col -> col));
        for (String name : oneMap.keySet()) {
            if (!otherMap.containsKey(name)) {
                return false;
            }
            if (!isSameIndex(oneMap.get(name), otherMap.get(name))) {
                return false;
            }
        }
        return true;
    }

    protected boolean isSameIndex(Index one, Index other) {
        boolean same = one.name().equals(other.name()) && (one.unique() == other.unique());
        if (same) {
            return isSameColumns(one.columns(), other.columns());
        }
        return false;
    }

    protected boolean isSameColumn(Column one, Column other) {
        boolean same = one.autoIncrement() == other.autoIncrement() && one.nullable() == other.nullable() && one.key() == other.key() && one.name().equals(other.name()) && ((one.defaultValue() != null && other.defaultValue() != null && one.defaultValue().equals(other.defaultValue())) || (one.defaultValue() == null && other.defaultValue() == null)) && modeller.typesAreCompatible(one, other);
        if (!same) {
            say("one %s\n\tvs\nother %s", one, other);

        }
        return same;
    }

}


