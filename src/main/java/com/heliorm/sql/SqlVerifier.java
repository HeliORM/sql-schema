package com.heliorm.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tool for verifying if a user supplied SQL data structure is the same as the one in a database.
 */
public final class SqlVerifier {

    private final SqlModeller modeller;
    private final boolean deleteMissingColumns;
    private final boolean deleteMissingIndexes;

    /**
     * Create a new verifier for the supplied SQL modeller.
     *
     * @param modeller The modeller to use
     * @return The verifier
     */
    public static SqlVerifier forModeller(SqlModeller modeller, boolean deleteMissingColumns, boolean deleteMissingIndexes) {
        return new SqlVerifier(modeller, deleteMissingColumns, deleteMissingIndexes);
    }

    public static SqlVerifier forModeller(SqlModeller modeller) {
        return forModeller(modeller, false, false);
    }

    /**
     * Verify that a table in a SQL database is the same as the abstraction supplied, and change the database
     * to conform if not.
     *
     * @param table The table
     * @return The changes made to synchronize the table.
     */
    public List<Action> synchronizeDatabaseTable(Table table) throws SqlModellerException {
        if (!modeller.tableExists(table)) {
            modeller.createTable(table);
            return Collections.singletonList(Action.createTable(table));
        } else {
            var actions = new ArrayList<Action>();
            actions.addAll(synchronizeColumns(table));
            actions.addAll(synchronizeIndexes(table));
            return actions;
        }
    }

    private List<Action> synchronizeColumns(Table table) throws SqlModellerException {
        Table sqlTable = modeller.readTable(table.getDatabase(), table.getName());
        Map<String, Column> tableColumns = table.getColumns().stream()
                .collect(Collectors.toMap(Column::getName, col -> col));
        Map<String, Column> sqlColumns = sqlTable.getColumns().stream()
                .collect(Collectors.toMap(Column::getName, col -> col));
        List<Action> actions = new ArrayList<>();
        for (var name : tableColumns.keySet()) {
            var tableColumn = tableColumns.get(name);
            if (sqlColumns.keySet().stream().noneMatch(key -> key.equalsIgnoreCase(name))) {
                modeller.addColumn(tableColumn);
                actions.add(Action.addColumn(tableColumn));
            } else {
                var opt = sqlColumns.keySet().stream().filter(key -> key.equalsIgnoreCase(name))
                        .map(sqlColumns::get).findFirst();
                if (opt.isPresent()) {
                    var sqlColumn = opt.get();
                    if (!sqlColumn.getName().equals(tableColumn.getName())) {
                        modeller.renameColumn(sqlColumn, tableColumn);
                        actions.add(Action.renameColumn(sqlColumn, tableColumn));
                    }
                    if (isNotSame(tableColumn, sqlColumn)) {
                        modeller.modifyColumn(sqlColumn, tableColumn);
                        actions.add(Action.modifyColumn(tableColumn));
                    }
                }
            }
        }
        for (var name : sqlColumns.keySet()) {
            var sqlColumn = sqlColumns.get(name);
            if (!tableColumns.containsKey(name)) {
                if (deleteMissingColumns) {
                    modeller.deleteColumn(sqlColumn);
                    actions.add(Action.deleteColumn(sqlColumn));
                } else {
                    if (!sqlColumn.isNullable()) {
                        if (sqlColumn instanceof SqlColumn) {
                            ((SqlColumn) sqlColumn).setNullable(true);
                            modeller.modifyColumn(sqlColumn);
                            actions.add(Action.modifyColumn(sqlColumn));
                        }
                    }
                }
            }
        }
        return actions;
    }

    private List<Action> synchronizeIndexes(Table table) throws SqlModellerException {
        Table sqlTable = modeller.readTable(table.getDatabase(), table.getName());
        Map<String, Index> tableIndexes = table.getIndexes().stream()
                .collect(Collectors.toMap(Index::getName, col -> col));
        Map<String, Index> sqlIndexes = sqlTable.getIndexes().stream()
                .collect(Collectors.toMap(Index::getName, col -> col));
        List<Action> actions = new ArrayList<>();
        for (String name : tableIndexes.keySet()) {
            Index tableIndex = tableIndexes.get(name);
            if (!sqlIndexes.containsKey(name)) {
                modeller.addIndex(tableIndex);
                actions.add(Action.addIndex(tableIndex));
            } else {
                Index sqlIndex = sqlIndexes.get(name);
                if (!isSame(tableIndex, sqlIndex)) {
                    modeller.modifyIndex(tableIndex);
                    actions.add(Action.modifyIndex(tableIndex));
                }
            }
        }
        for (String name : sqlIndexes.keySet()) {
            Index sqlIndex = sqlIndexes.get(name);
            if (!tableIndexes.containsKey(name)) {
                if (deleteMissingIndexes) {
                    modeller.removeIndex(sqlIndex);
                    actions.add(Action.deleteIndex(sqlIndex));
                }
            }
        }
        return actions;
    }

    private boolean isNotSame(Column one, Column other) {
        return one.isAutoIncrement() != other.isAutoIncrement()
                || one.isNullable() != other.isNullable()
                || one.isKey() != other.isKey()
                || !one.getName().equals(other.getName())
                || !modeller.typesAreCompatible(one, other)
                || !isSameDefault(one, other);
    }

    private boolean isSame(Index one, Index other) {
        boolean same = one.getName().equals(other.getName())
                && (one.isUnique() == other.isUnique());
        if (same) {
            return isSame(one.getColumns(), other.getColumns());
        }
        return false;
    }

    private boolean isSame(Set<Column> one, Set<Column> other) {
        if (one.size() != other.size()) {
            return false;
        }
        Map<String, Column> oneMap = one.stream().collect(Collectors.toMap(Column::getName, col -> col));
        Map<String, Column> otherMap = other.stream().collect(Collectors.toMap(Column::getName, col -> col));
        for (String name : oneMap.keySet()) {
            if (!otherMap.containsKey(name)) {
                return false;
            }
            if (isNotSame(oneMap.get(name), otherMap.get(name))) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameDefault(Column one, Column other) {
        if (one.getDefault() == null) {
            return other.getDefault() == null;
        }
        if (other.getDefault() == null) {
            return false;
        }
        return switch (one) {
            case BooleanColumn bc -> booleanValue(bc) == booleanValue(other);
            case BitColumn bc -> switch (other) {
                case BooleanColumn obc -> booleanValue(bc) == booleanValue(obc);
                default -> one.getDefault().equals(other.getDefault());
            };
            case IntegerColumn ic -> switch (other) {
                case BooleanColumn bc -> booleanValue(bc) == booleanValue(ic);
                default -> one.getDefault().equals(other.getDefault());
            };
            default -> one.getDefault().equals(other.getDefault());
        };
    }

    private boolean booleanValue(Column column) {
        return switch (column.getDefault()) {
            case "1", "TRUE", "true" -> true;
            default -> false;
        };
    }

    private SqlVerifier(SqlModeller modeller, boolean deleteMissingColumns, boolean deleteMissingIndexes) {
        this.modeller = modeller;
        this.deleteMissingColumns = deleteMissingColumns;
        this.deleteMissingIndexes = deleteMissingIndexes;
    }
}
