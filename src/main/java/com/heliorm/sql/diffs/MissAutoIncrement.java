package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;
import com.heliorm.sql.actions.Action;

public record MissAutoIncrement(Column column) implements Diff {
}
