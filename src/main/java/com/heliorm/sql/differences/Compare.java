package com.heliorm.sql.differences;

import com.heliorm.sql.BinaryColumn;
import com.heliorm.sql.BitColumn;
import com.heliorm.sql.BooleanColumn;
import com.heliorm.sql.Column;
import com.heliorm.sql.DateTimeColumn;
import com.heliorm.sql.DecimalColumn;
import com.heliorm.sql.DoubleColumn;
import com.heliorm.sql.EnumColumn;
import com.heliorm.sql.IntegerColumn;
import com.heliorm.sql.SetColumn;
import com.heliorm.sql.StringColumn;
import com.heliorm.sql.Table;
import com.heliorm.sql.TimeStampColumn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Compare {

    public static List<Action> compare(Table have, Table want) {
        var res = new ArrayList<Action>();
        var haveColumns = columnNames(have);
        var wantColumns = columnNames(want);
        var bothColumns = Stream.concat(haveColumns.stream().filter(wantColumns::contains),
        wantColumns.stream().filter(haveColumns::contains)).collect(Collectors.toSet());
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
            case StringColumn sc -> compareString(sc, want);
            case SetColumn sc -> compareSet(sc, want);
            case BitColumn bc -> compareBit(bc, want);
            case BooleanColumn bc -> compareBoolean(bc, want);
            case DecimalColumn dc -> compareDecimal(dc, want);
            case DateTimeColumn dc -> compareDateTime(dc, want);
            case DoubleColumn dc -> compareDouble(dc, want);
            case BinaryColumn bc -> compareBinary(bc, want);
            case TimeStampColumn tc -> compareTimeStamp(tc, want);
            case IntegerColumn ic -> compareInteger(ic, want);
        };
    }

    private static List<Action> compareEnum(EnumColumn have, Column want) {
        if (want instanceof EnumColumn ec) {
            return append(compareEnumValues(have, ec), compareDefault(have, want));
        } else {
            return List.of(new ChangeType(want));
        }
    }

    private static List<Action> compareString(StringColumn have, Column want) {
        if (want instanceof StringColumn sc) {
            return append(compareLength(have, sc), compareDefault(have, sc));
        } else {
            return List.of(new ChangeType(want));
        }
    }

    private static List<Action> compareSet(SetColumn have, Column want) {
        if (want instanceof SetColumn sc) {
            return append(compareSetValues(have, sc), compareDefault(have, want));
        }
        return List.of(new ChangeType(want));
    }

    private static List<Action> compareBit(BitColumn have, Column want) {
        return switch (want) {
            case BitColumn bc -> {
                if (bc.getBits() > have.getBits()) {
                    yield append(List.of(new IncreaseLength(want)), compareDefault(have, want));
                }
                if (bc.getBits() < have.getBits()) {
                    yield append(List.of(new DecreaseLength(want)), compareDefault(have, want));
                }
                yield compareDefault(have, want);
            }
            case BooleanColumn bc -> {
                if (have.getBits() == 1) {
                    yield compareDefault(have,want);
                }
                yield List.of(new ChangeType(want));
            }
            default -> List.of(new ChangeType(want));
        };
    }

    private static List<Action> compareBoolean(BooleanColumn have, Column want) {
        return switch (want) {
            case BooleanColumn bc -> compareDefault(have, want);
            case BitColumn bc -> {
                if (bc.getBits() == 1) {
                    yield compareDefault(have, want);
                }
                yield List.of(new ChangeType(want));
            }
            default -> List.of(new ChangeType(want));
        };
    }

    private static List<Action> compareDecimal(DecimalColumn have, Column want) {
        if (want instanceof DecimalColumn dc) {
            if (have.getPrecision() != dc.getPrecision() || have.getScale() != dc.getScale()) {
                return List.of(new ChangeType(want));
            }
            return compareDefault(have, want);

        }
        return List.of(new ChangeType(want));
    }

    private static List<Action> compareDateTime(DateTimeColumn have, Column want) {
        if (want instanceof DateTimeColumn dtc) {
            return compareDefault(have,want);
        }
        return List.of(new ChangeType(want));
    }

    private static List<Action> compareDouble(DoubleColumn have, Column want) {
        if (want instanceof DoubleColumn dc) {
            return compareDefault(have, want);
        }
        return List.of(new ChangeType(want));
    }

    private static List<Action> compareBinary(BinaryColumn have, Column want) {
        if (want instanceof BinaryColumn bc) {
            if (have.getLength() > bc.getLength()) {
                return List.of(new DecreaseLength(want));
            }
            if (have.getLength() < bc.getLength()) {
                return List.of(new IncreaseLength(want));
            }
            return List.of();
        }
        return List.of(new ChangeType(want));
    }


    private static List<Action> compareTimeStamp(TimeStampColumn have, Column want) {
        if (want instanceof TimeStampColumn tc) {
            return compareDefault(have, want);
        }
        return List.of(new ChangeType(want));
    }

    private static List<Action> compareInteger(IntegerColumn have, Column want) {
        if (want instanceof IntegerColumn ic) {
            return  compareDefault(have, want);
        }
        return List.of(new ChangeType(want));
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

    private static List<Action> compareLength(StringColumn have, StringColumn want) {
        var haveLength = actualTextLength(have);
        var wantLength = actualTextLength(want);
        if (haveLength > wantLength) {
            return List.of(new DecreaseLength(want));
        }
        if (haveLength < wantLength) {
            return List.of(new IncreaseLength(want));
        }
        return List.of();
    }

    private static List<Action> compareSetValues(SetColumn have, SetColumn want) {
        var res = new ArrayList<Action>();
        var bothNames = new HashSet<>(have.getSetValues());
        bothNames.addAll(want.getSetValues());
        for (var name : bothNames) {
            if (have.getSetValues().contains(name) && !want.getSetValues().contains(name)) {
                res.add(new RemoveSetValue(name));
            }
            if (!have.getSetValues().contains(name) && want.getSetValues().contains(name)) {
                res.add(new RemoveEnumValue(name));
            }
        }
        return res;
    }

    private static List<Action> compareDefault(Column have, Column want) {
        if (have.getDefault() == null) {
            if (want.getDefault() == null) {
                return List.of();
            }
            else {
                return List.of(new AddDefault(want));
            }
        }
        if (want.getDefault() == null) {
            return List.of( new RemoveDefault(want));
        }
        if (want.getDefault().equals(have.getDefault())) {
            return List.of();
        }
        return List.of(new ChangeDefault(want));
    }

    private static int actualTextLength(StringColumn column) {
        var length = column.getLength();
        if (length > 16777215) {
            return 2147483647;
        } else if (length > 65535) {
            return 16777215;
        } else if (length > 255) {
            return 65535;
        }
        return length;
    }

    private static Set<String> columnNames(Table table) {
        return table.getColumns()
                .stream()
                .map(Column::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static List<Action> append(List<Action> first, List<Action> second) {
        return Stream.concat(first.stream(), second.stream()).toList();
    }

    private Compare() {
    }
}
