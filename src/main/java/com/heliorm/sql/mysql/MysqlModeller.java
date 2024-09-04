package com.heliorm.sql.mysql;

import com.heliorm.sql.BinaryColumn;
import com.heliorm.sql.BitColumn;
import com.heliorm.sql.BooleanColumn;
import com.heliorm.sql.Column;
import com.heliorm.sql.Database;
import com.heliorm.sql.DateTimeColumn;
import com.heliorm.sql.DecimalColumn;
import com.heliorm.sql.DoubleColumn;
import com.heliorm.sql.EnumColumn;
import com.heliorm.sql.Index;
import com.heliorm.sql.SetColumn;
import com.heliorm.sql.SqlModeller;
import com.heliorm.sql.SqlModellerException;
import com.heliorm.sql.StringColumn;
import com.heliorm.sql.Table;
import com.heliorm.sql.TimeStampColumn;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * An implementation of the SQL modeller that deals with MySQL/MariaDB syntax.
 */
public final class MysqlModeller extends SqlModeller {
    private final boolean anonymousDb;

    /**
     * Create a new modeller with the given connection supplier.
     *
     * @param supplier The connection supplier
     */
    public MysqlModeller(Supplier<Connection> supplier, boolean anonymousDb) {
        super(supplier);
        this.anonymousDb = anonymousDb;
    }

    @Override
    protected String makeCreateTableQuery(Table table) {
        var body = new StringJoiner(",");
        for (var column : table.getColumns()) {
            body.add(format("%s %s", getColumnName(column), getCreateType(column)));
        }
        for (var index : table.getIndexes()) {
            body.add(format("%sKEY %s (%s)",
                    index.isUnique() ? "UNIQUE " : "",
                    getIndexName(index),
                    index.getColumns().stream()
                            .map(this::getColumnName)
                            .reduce((c1, c2) -> c1 + "," + c2).get()));
        }
        return format("CREATE TABLE %s (", getTableName(table)) +
                body +
                ")";
    }

    @Override
    public void modifyIndex(Index index) throws SqlModellerException {
        removeIndex(index);
        addIndex(index);
    }

    @Override
    public boolean supportsSet() {
        return true;
    }

    @Override
    protected boolean isEnumColumn(String columnName, JDBCType jdbcType, String typeName) {
        return typeName.equals("ENUM");
    }

    @Override
    protected String getDatabaseName(Database database) {
        return format("`%s`", database.getName());
    }

    @Override
    protected String getTableName(Table table) {
        return anonymousDb ? format("`%s`", table.getName()) : format("`%s`.`%s`", table.getDatabase().getName(), table.getName());
    }

    @Override
    protected String getCreateType(Column column) {
        return getCreateType(column, false);
    }

    private String getCreateType(Column column, boolean skipKey) {
        String typeName = column.getJdbcType().getName();
        StringBuilder type = new StringBuilder();
        switch (column) {
            case EnumColumn ec -> {
                Set<String> enumValues = ec.getEnumValues();
                typeName = "ENUM("
                        + enumValues.stream()
                        .map(val -> "'" + val + "'")
                        .reduce((v1, v2) -> v1 + "," + v2).get()
                        + ")";
            }
            case SetColumn ec -> {
                Set<String> values = ec.getSetValues();
                typeName = "SET("
                        + values.stream()
                        .map(val -> "'" + val + "'")
                        .reduce((v1, v2) -> v1 + "," + v2).get()
                        + ")";
            }
            case StringColumn stringColumn -> {
                int length = stringColumn.getLength();
                if (length >= 16777215) {
                    typeName = "LONGTEXT";
                } else if (length > 65535) {
                    typeName = "MEDIUMTEXT";
                } else if (length > 255) {
                    typeName = "TEXT";
                } else {
                    typeName = format("VARCHAR(%d)", length);
                }
            }
            case DecimalColumn decimalColumn ->
                    typeName = format("DECIMAL(%d,%d)", decimalColumn.getPrecision(), decimalColumn.getScale());
            case BinaryColumn binaryColumn -> {
                int length = binaryColumn.getLength();
                if (length >= 16777215) {
                    typeName = "LONGBLOB";
                } else if (length > 65535) {
                    typeName = "MEDIUMBLOB";
                } else if (length > 255) {
                    typeName = "BLOB";
                } else {
                    typeName = "TINYBLOB";
                }
            }
            case DateTimeColumn ignored -> typeName = "DATETIME";
            case TimeStampColumn ignored -> typeName = "TIMESTAMP";
            case DoubleColumn ignored -> typeName = "DOUBLE";
            default -> {
            }
        }
        type.append(typeName);
        if (!column.isNullable()) {
            type.append(" NOT NULL");
        }
        if ((column.getDefault() != null) && !column.isAutoIncrement()) {
            type.append(" DEFAULT ").append(switch (column) {
                case StringColumn sc -> "'" + sc.getDefault() + "'";
                case EnumColumn ec -> "'" + ec.getDefault() + "'";
                default -> column.getDefault();
            });
        }
        if (!skipKey) {
            if (column.isAutoIncrement()) {
                type.append(" AUTO_INCREMENT");
            }
            if (column.isKey()) {
                type.append(" PRIMARY KEY");
            }
        }
        return type.toString();
    }

    @Override
    protected String getColumnName(Column column) {
        return format("`%s`", column.getName());
    }

    @Override
    protected String getIndexName(Index index) {
        return format("`%s`", index.getName());
    }

    @Override
    protected Set<String> readEnumValues(EnumColumn column) throws SqlModellerException {
        String query = format("SELECT SUBSTRING(COLUMN_TYPE,5) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='%s' " +
                        "AND TABLE_NAME='%s' AND COLUMN_NAME='%s'",
                column.getTable().getDatabase().getName(),
                column.getTable().getName(),
                column.getName());
        try (Connection con = con(); Statement stmt = con.createStatement(); ResultSet ers = stmt.executeQuery(query)) {
            if (ers.next()) {
                return Arrays.stream(ers.getString(1).replace("enum", "").replace("(", "").replace(")", "")
                                .split(","))
                        .map(val -> val.substring(1, val.length() - 1))
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (SQLException ex) {
            throw new SqlModellerException(format("Error reading enum values from %s.%s.%s (%s)",
                    column.getTable().getDatabase().getName(), column.getTable().getName(), column.getName(), ex.getMessage()), ex);
        }
    }

    @Override
    protected String makeRenameIndexQuery(Index current, Index changed) {
        return format("ALTER TABLE %s RENAME INDEX %s TO %s", getTableName(current.getTable()), getIndexName(current), getIndexName(changed));
    }

    @Override
    protected boolean isSetColumn(String columnName, JDBCType jdbcType, String typeName) {
        return typeName.equals("SET");
    }

    @Override
    protected String makeReadSetQuery(SetColumn column) {
        return format("SELECT SUBSTRING(COLUMN_TYPE,5) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='%s' " +
                "AND TABLE_NAME='%s' AND COLUMN_NAME='%s'", column.getTable().getDatabase().getName(), column.getTable().getName(), column.getName());
    }

    @Override
    protected Set<String> extractSetValues(String text) {
        return Arrays.stream(text.replace("enum", "").replace("(", "").replace(")", "")
                        .split(","))
                .map(val -> val.substring(1, val.length() - 1))
                .collect(Collectors.toSet());
    }

    protected List<String> makeModifyColumnQuery(Column column) {
        var res = new ArrayList<String>();
        if (column.isKey()) {
            res.add(format("ALTER TABLE %s MODIFY COLUMN %s %s",
                    getTableName(column.getTable()),
                    getColumnName(column),
                    getCreateType(column, true)) + ", DROP PRIMARY KEY;");
        }
        res.add(format("ALTER TABLE %s MODIFY COLUMN %s %s",
                getTableName(column.getTable()),
                getColumnName(column),
                getCreateType(column)));
        return res;
    }

    @Override
    protected List<String> makeModifyColumnQuery(Column current, Column changed) {
        var res = new ArrayList<String>();
        if (current.isKey()) {
            res.add(format("ALTER TABLE %s MODIFY COLUMN %s %s",
                    getTableName(current.getTable()),
                    getColumnName(current),
                    getCreateType(current, true)) + ", DROP PRIMARY KEY;");
        }
        res.add(format("ALTER TABLE %s MODIFY COLUMN %s %s",
                getTableName(changed.getTable()),
                getColumnName(changed),
                getCreateType(changed)));
        return res;
    }

    @Override
    protected String makeAddColumnQuery(Column column) {
        return format("ALTER TABLE %s ADD COLUMN %s %s",
                getTableName(column.getTable()),
                getColumnName(column),
                getCreateType(column));
    }

    @Override
    protected String makeRemoveIndexQuery(Index index) {
        return format("DROP INDEX %s on %s",
                getIndexName(index),
                getTableName(index.getTable()));
    }

    @Override
    protected String makeModifyIndexQuery(Index index) {
        return format("ALTER %sINDEX %s ON %s %s",
                index.isUnique() ? "UNIQUE " : "",
                getIndexName(index),
                getTableName(index.getTable()),
                index.getColumns().stream()
                        .map(this::getColumnName)
                        .reduce((c1, c2) -> c1 + "," + c2).get());
    }

    @Override
    protected boolean typesAreCompatible(Column one, Column other) {
        if (one instanceof EnumColumn) {
            if (other instanceof EnumColumn) {
                Set<String> ones = ((EnumColumn) one).getEnumValues();
                Set<String> others = ((EnumColumn) other).getEnumValues();
                return others.containsAll(ones)
                        && (ones.containsAll(others));
            }
            return false;
        } else if (one instanceof SetColumn) {
            if (other instanceof SetColumn) {
                Set<String> ones = ((SetColumn) one).getSetValues();
                Set<String> others = ((SetColumn) other).getSetValues();
                return others.containsAll(ones)
                        && (ones.containsAll(others));
            }
            return false;
        } else if (one instanceof BitColumn) {
            if (other instanceof BitColumn) {
                return ((BitColumn) one).getBits() == ((BitColumn) other).getBits();
            }
            if (other instanceof BooleanColumn) {
                return ((BitColumn) one).getBits() == 1;
            }
            return false;
        } else if (one instanceof BooleanColumn) {
            if (other instanceof BooleanColumn) {
                return true;
            } else if (other instanceof BitColumn) {
                return ((BitColumn) other).getBits() == 1;
            }
            return false;
        } else if (one instanceof StringColumn) {
            if (other instanceof StringColumn) {
                return actualTextLength((StringColumn) one) == actualTextLength((StringColumn) other);
            }
            return false;
        } else if (one instanceof DecimalColumn) {
            if (other instanceof DecimalColumn) {
                return ((DecimalColumn) one).getPrecision() == ((DecimalColumn) other).getPrecision()
                        && ((DecimalColumn) one).getScale() == ((DecimalColumn) other).getScale();
            }
            return false;
        } else if (one instanceof DateTimeColumn) {
            if (other instanceof DateTimeColumn) {
                return true;
            }
        } else if (one instanceof DoubleColumn) {
            if (other instanceof DoubleColumn) {
                return true;
            }
        } else if (one instanceof BinaryColumn bin1) {
            if (other instanceof BinaryColumn bin2) {
                return actualLength(bin1) == actualLength(bin2);
            }
            return false;
        }
        return one.getJdbcType() == other.getJdbcType();
    }

    @Override
    protected String extractDefault(String text) {
        return switch (text) {
            case "NULL" -> null;
            case "''" -> "";
            default -> {
                if (text.length() == 1) {
                    yield text;
                } else {
                    if (text.startsWith("'") && text.endsWith("'")) {
                        yield text.substring(1, text.length() - 1);
                    }
                }
                yield text;
            }
        };
    }
}
