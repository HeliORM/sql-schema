package com.heliorm.sql.differences;

import com.heliorm.sql.Column;
import com.heliorm.sql.EnumColumn;
import com.heliorm.sql.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class Compare {

    static List<Action> compare(Table have, Table want) {
        var res =new ArrayList<Action>();
        var haveColumns = columnNames(have);
        var wantColumns = columnNames(want);
        var bothColumns = new HashSet<>(haveColumns);
        bothColumns.addAll(wantColumns);
        for (var name : haveColumns) {
            if (want.getColumn(name) == null) {
                res.add(new RemoveColumn(have.getColumn(name)));
            }
        }
        for (var name : wantColumns) {
            if (have.getColumn(name) == null) {
                res.add(new AddColumn(want.getColumn(name)));
            }
        }
        for (var name : bothColumns) {
            res.addAll(compare(have.getColumn(name), want.getColumn(name)));
        }
        return res;
    }

    private static List<Action> compare(Column have, Column want) {
        var res = new ArrayList<Action>();
        res.addAll(compareName(have, want));
        res.addAll(compareAutoIncrement(have, want));
        res.addAll(compareNullable(have, want));
        res.addAll(compareKey(have, want));
        res.addAll(compareType(have, want));

        /*
                || !isSameDefault(one, other);
         */
        return res;
    }

    private static List<Action> compareName(Column have, Column want) {
        return !have.getName().equals(want.getName())
                ? List.of(new RenameColumn(want)) : List.of();
    }

    private static List<Action> compareAutoIncrement(Column have, Column want) {
        if (have.isAutoIncrement() && !want.isAutoIncrement()) {
            return List.of(new RemoveAutoIncrement(want));
        }
        if (!have.isAutoIncrement() && want.isAutoIncrement()) {
            return List.of(new AddAutoIncrement(want));
        }
        return List.of();
    }

    private static List<Action> compareNullable(Column have, Column want) {
        if (have.isNullable() && !want.isNullable()) {
            return List.of(new RemoveNullable(want));
        }
        if (!have.isNullable() && want.isNullable()) {
            return List.of(new AddNullable(want));
        }
        return List.of();
    }

    private static List<Action> compareKey(Column have, Column want) {
        if (have.isKey() && !want.isKey()) {
            return List.of(new RemoveKey(want));
        }
        if (!have.isKey() && want.isKey()) {
            return List.of(new RemoveKey(want));
        }
        return List.of();
    }

    private static List<Action> compareType(Column have, Column want) {
        return switch (have) {
            case EnumColumn ec -> compareEnum(ec, want);
        };
    }

    private static List<Action> compareEnum(EnumColumn have, Column want) {
        if (want instanceof EnumColumn ec) {
            return compareEnumValues(have, ec);
        }
        else {
            return List.of(new ChangeType(want));
        }
    }

    private static List<Action> compareEnumValues(EnumColumn have, EnumColumn want) {
        var res = new ArrayList<Action>();
        var bothNames = new HashSet<>(have.getEnumValues());
        bothNames.addAll(want.getEnumValues());
        for (var name : bothNames) {
            if (have.getEnumValues().contains(name) && !want.getEnumValues().contains(name)) {
                res.add(new RemoveEnumValue(name));
            }
            if (!have.getEnumValues().contains(name) && want.getEnumValues().contains(name)) {
                res.add(new AddEnumValue(name));
            }
        }
        return res;
    }

    private static Set<String> columnNames(Table table) {
        return table.getColumns()
                .stream()
                .map(Column::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private Compare() {}
}
