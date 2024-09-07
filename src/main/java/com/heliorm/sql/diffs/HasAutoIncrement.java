package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;
import com.heliorm.sql.actions.Action;

public record HasAutoIncrement(Column column) implements Diff {
}
