package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record MissNullable(Column column) implements Diff {
}
