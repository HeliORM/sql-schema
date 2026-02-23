package com.heliorm.sql;

import static java.lang.String.format;

/** An action taken by the verifier to bring a database into sync with what is expected by the database model. This can
 * be used to tell the user what was done to the database */
public record Action(Type type, String message) {

    public enum Type {
        CREATE_TABLE,
        ADD_COLUMN,
        DELETE_COLUMN,
        MODIFY_COLUMN,
        RENAME_COLUMN,
        ADD_INDEX,
        MODIFY_INDEX,
        DELETE_INDEX
    }


    static Action modifyColumn(Column column) {
        return new Action(Type.MODIFY_COLUMN, format("Modified column %s in table %s in database %s",
                column.name(),
                column.table().getName(),
                column.table().getDatabase().name()));
    }

    static Action renameColumn(Column current, Column changed) {
        return new Action(Type.RENAME_COLUMN, format("Renamed column %s to %s in table %s in database %s",
                current.name(),
                changed.name(),
                current.table().getName(),
                current.table().getDatabase().name()));
    }

    static Action deleteColumn(Column column) {
        return new Action(Type.DELETE_COLUMN, format("Deleted column %s from table %s in database %s",
                column.name(),
                column.table().getName(),
                column.table().getDatabase().name()));
    }

    static Action addColumn(Column column) {
        return new Action(Type.ADD_COLUMN, format("Added column %s to table %s in database %s",
                column.name(),
                column.table().getName(),
                column.table().getDatabase().name()));
    }

    static Action createTable(Table table) {
        return new Action(Type.CREATE_TABLE,
                format("Created table %s in database %s",
                        table.getName(), table.getDatabase().name()));
    }

    static Action addIndex(Index index) {
        return new Action(Type.ADD_INDEX,
                format("Created index %s on table %s in database %s",
                        index.name(),
                        index.table().getName(),
                        index.table().getDatabase().name()));
    }

     static Action deleteIndex(Index index) {
         return new Action(Type.DELETE_INDEX, format("Deleted index %s from table %s in database %s",
                 index.name(),
                 index.table().getName(),
                 index.table().getDatabase().name()));
    }

    static Action modifyIndex(Index index) {
        return new Action(Type.MODIFY_INDEX, format("Modified index %s in table %s in database %s",
                index.name(),
                index.table().getName(),
                index.table().getDatabase().name()));
    }


    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}